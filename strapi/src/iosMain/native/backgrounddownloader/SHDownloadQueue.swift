import Foundation

// struct Queue<T> {
//     @Atomic private(set) var elements: [T] = []
//
//     mutating func enqueue(_ value: T) {
//         elements.append(value)
//     }
//
//     mutating func dequeue() -> T? {
//         guard !elements.isEmpty else {
//             return nil
//         }
//
//         return elements.removeFirst()
//     }
// }

final class Queue<T> {
    private(set) var elements: [T] = []
    private let queue = DispatchQueue(label: "queue.serial")

    func enqueue(_ value: T) {
        queue.sync {
            elements.append(value)
        }
    }

    func dequeue() -> T? {
        return queue.sync {
            guard !elements.isEmpty else { return nil }
            return elements.removeFirst()
        }
    }
}
