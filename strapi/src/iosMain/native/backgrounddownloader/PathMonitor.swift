import Foundation
import Network

@objc public final class PathMonitor: NSObject {
    
    @objc public static let shared = PathMonitor()
    
    @objc var isExpensive: Bool {
        return pathMonitor.currentPath.isExpensive
    }

    private var pathMonitor = NWPathMonitor()

    private override init() { }
    
    @objc public func startMonitoring() {
        pathMonitor = NWPathMonitor()
        pathMonitor.start(queue: DispatchQueue(label: "NetworkMonitor"))
        pathMonitor.pathUpdateHandler = { _ in

        }
    }
}
