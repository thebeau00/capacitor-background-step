import Foundation
import Capacitor
import HealthKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BackgroundstepPlugin)
public class BackgroundstepPlugin: CAPPlugin, CAPBridgedPlugin {
    
    private let healthStore = HKHealthStore()
    
	public let identifier = "BackgroundstepPlugin"
	public let jsName = "Backgroundstep"
	public let pluginMethods: [CAPPluginMethod] = [
		CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise),
		CAPPluginMethod(name: "serviceStart", returnType: CAPPluginReturnPromise),
		CAPPluginMethod(name: "serviceStop", returnType: CAPPluginReturnPromise),
		CAPPluginMethod(name: "getToday", returnType: CAPPluginReturnPromise),
		CAPPluginMethod(name: "getStepData", returnType: CAPPluginReturnPromise),
		CAPPluginMethod(name: "checkAndRequestPermission", returnType: CAPPluginReturnPromise)
	]
	private let implementation = Backgroundstep()

	@objc func echo(_ call: CAPPluginCall) {
		let value = call.getString("value") ?? ""
		call.resolve([
			"value": implementation.echo(value)
		])
	}

	@objc func serviceStart(_ call: CAPPluginCall) {
		let success = implementation.startService()
		call.resolve([
			"res": success
		])
	}
	
	@objc func serviceStop(_ call: CAPPluginCall) {
		let success = implementation.stopService()
		call.resolve([
			"res": success
		])
	}
	
	@objc func getToday(_ call: CAPPluginCall) {
		implementation.getTodayStepCount { stepCount, error in
			if let error = error {
					call.reject("Failed to fetch today's step count: \(error)")
			} else if let stepCount = stepCount {
					call.resolve([
						"count": stepCount
					])
			} else {
					call.reject("Unknown error occurred while fetching today's step count")
			}
		}
	}
		
    @objc func getStepData(_ call: CAPPluginCall) {
        guard let sDateTime = call.getString("sDateTime"), let eDateTime = call.getString("eDateTime") else {
            call.reject("Invalid date parameters")
            return
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"

        guard let startDate = dateFormatter.date(from: sDateTime), let endDate = dateFormatter.date(from: eDateTime) else {
            call.reject("Invalid date format")
            return
        }
        
        implementation.getStepData(startDate: startDate, endDate: endDate) { stepCount, error in
            if let error = error {
                call.reject("Failed to fetch step count data: \(error)")
            } else if let stepCount = stepCount {
                call.resolve([
                    "count": stepCount
                ])
            } else {
                call.reject("Unknown error occurred while fetching step count data")
            }
        }
    }
	
	@objc func checkAndRequestPermission(_ call: CAPPluginCall) {
		implementation.checkAndRequestPermission { granted in
			call.resolve([
				"granted": granted
			])
		}
	}

}
