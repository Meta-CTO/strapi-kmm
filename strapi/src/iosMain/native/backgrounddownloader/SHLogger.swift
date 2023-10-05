import Foundation

/// SHLogger enum used to log messages (warning / error / verbose)
public enum SHLogger {
    
    /// Desired logging level (warning / error / verbose)
    public struct Level: OptionSet {
        public static let warning = Level(rawValue: 1 << 0)
        public static let error = Level(rawValue: 1 << 1)
        public static let verbose: Level = [.warning, .error]
        
        public let rawValue: UInt

        public init(rawValue: UInt) {
            self.rawValue = rawValue
        }
    }
    
    /// Current log level
    private static var level: Level = .verbose
    
    /// Configure function to set the current log level
    ///
    /// - Parameter level: Desired log level
    static func configure(with level: Level) {
        Self.level = level
    }
    
    /// Log function used to log messages to the console
    ///
    /// - Parameters:
    ///   - message: Required message to log to the console
    ///   - level: The level of the message
    static func log(_ message: String, level: Level) {
        if Self.level.contains(level) {
            print(message)
        }
    }
}
