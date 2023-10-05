import Foundation

struct Queue<T> {
    @Atomic private(set) var elements: [T] = []
    
    mutating func enqueue(_ value: T) {
        elements.append(value)
    }
    
    mutating func dequeue() -> T? {
        guard !elements.isEmpty else {
            return nil
        }
        
        return elements.removeFirst()
    }
}
