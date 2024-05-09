import Foundation

public protocol Asset {
    var url: URL? { get }
    var fileExtension: String { get }
    var identifier: String { get }
}

public extension Asset {
    
    var fileExtension: String {
        guard
            let url
        else {
            assertionFailure("Unsupported configuration, URL is nil")
            return ""
        }
        
        let pathExtension = url.pathExtension
        
        if pathExtension.isEmpty {
            assertionFailure("Unsupported URL because its pathExtension is empty, please override fileExtension property and provide the proper fileExtension")
        }
        
        return pathExtension
    }
    
    var identifier: String {
        guard
            let url
        else {
            assertionFailure("Unsupported configuration, URL is nil")
            return ""
        }
        
        if url.pathExtension.isEmpty && fileExtension.isEmpty {
            assertionFailure("Unsupported URL because its pathExtension is empty, please override fileExtension property and provide the proper fileExtension")
        }
        
        let fileName = url
            .removingQueries
            .absoluteString
            .components(separatedBy: "://")
            .last?
            .replacingOccurrences(of: "/", with: "-")
        
        if url.pathExtension.isEmpty {
            return "\(fileName ?? "").\(fileExtension)"
        }
        
        return fileName ?? ""
    }
    
}
