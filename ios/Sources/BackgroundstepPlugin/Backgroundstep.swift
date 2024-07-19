import Foundation

@objc public class Backgroundstep: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
