import Foundation

@propertyWrapper
class Atomic<Value> {
    private var value: Value
    private let queue = DispatchQueue(label: "com.shmediacache.atomic", attributes: .concurrent)

    var wrappedValue: Value {
        get {
            queue.sync {
                value
            }
        } set {
            queue.async(flags: .barrier) {
                self.value = newValue
            }
        }
    }

    init(wrappedValue value: Value) {
        self.value = value
    }
}
