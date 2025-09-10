import Foundation
import Capacitor
import HealthKit

@objc public class Backgroundstep: NSObject {

	private let healthStore = HKHealthStore()
    
	override init() {
		super.init()
//        requestAuthorization()
	}

	@objc public func echo(_ value: String) -> String {
		print(value)
		return value
	}

	private func requestAuthorization() {
		if HKHealthStore.isHealthDataAvailable() {
			let readTypes = Set([HKObjectType.quantityType(forIdentifier: .stepCount)!])
			
			healthStore.requestAuthorization(toShare: nil, read: readTypes) { (success, error) in
					if !success {
						print("HealthKit Authorization failed: \(String(describing: error))")
					}
			}
		}
	}
	
	@objc public func startService() -> Bool {
		return true
	}
	
	@objc public func stopService() -> Bool {
		return true
	}
	
	@objc public func getTodayStepCount(completionHandler: @escaping (NSNumber?, String?) -> Void) {
		
		let stepType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
		let calendar = Calendar.current
		let startDate = calendar.startOfDay(for: Date())
		let endDate = Date()
		let predicate = HKQuery.predicateForSamples(withStart: startDate, end: endDate, options: .strictStartDate)
        
		// Avoid manually logged data if applicable
		let predicateAvoidManuallyLoggedData = HKQuery.predicateForObjects(withMetadataKey: HKMetadataKeyWasUserEntered, operatorType: .notEqualTo, value: true)
		let compoundPredicate: NSCompoundPredicate = NSCompoundPredicate(andPredicateWithSubpredicates: [predicate, predicateAvoidManuallyLoggedData])
        
		let query = HKStatisticsQuery(quantityType: stepType, quantitySamplePredicate: compoundPredicate, options: .cumulativeSum) { query, statistics, error in
			var stepCount: Double = 0
			if let quantity = statistics?.sumQuantity() {
                stepCount = quantity.doubleValue(for: HKUnit.count())
			}
			DispatchQueue.main.async {
                completionHandler(NSNumber(value: stepCount), nil)
			}
		}
		
		healthStore.execute(query)
	}
	
	@objc public func getStepData(startDate: Date, endDate: Date, completionHandler: @escaping (NSNumber?, String?) -> Void) {
		
		let stepType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
		let datePredicate = HKQuery.predicateForSamples(withStart: startDate, end: endDate, options: .strictStartDate)

      // ✅ 수동 입력 데이터 제외
      // "UserEntered = true" 인 것만 제외 → 메타데이터가 없거나 false 인 경우는 포함
      let notUserEnteredPredicate = HKQuery.predicateForObjects(
          withMetadataKey: HKMetadataKeyWasUserEntered,
          operatorType: .notEqualTo,
          value: true
      )
      
      let compoundPredicate = NSCompoundPredicate(
          andPredicateWithSubpredicates: [datePredicate, notUserEnteredPredicate]
      )

		let query = HKStatisticsQuery(quantityType: stepType, quantitySamplePredicate: compoundPredicate, options: .cumulativeSum) { query, statistics, error in
			var stepCount: Double = 0
			if let quantity = statistics?.sumQuantity() {
					stepCount = quantity.doubleValue(for: HKUnit.count())
			}
			DispatchQueue.main.async {
					completionHandler(NSNumber(value: stepCount), nil)
			}
		}
		
		healthStore.execute(query)
	}
		
	@objc public func checkAndRequestPermission(completionHandler: @escaping (Bool) -> Void) {
		let readTypes = Set([HKObjectType.quantityType(forIdentifier: .stepCount)!])

		healthStore.requestAuthorization(toShare: nil, read: readTypes) { (success, error) in
			DispatchQueue.main.async {
				if success {
					// 사용자가 Step Count에 대한 권한을 허용했는지 확인
					let status = self.healthStore.authorizationStatus(for: HKObjectType.quantityType(forIdentifier: .stepCount)!)
					let isGranted = (status == .sharingAuthorized)
					completionHandler(isGranted)
				} else {
					// 권한 요청 자체가 실패한 경우
					completionHandler(false)
				}
			}
		}
	}

}
