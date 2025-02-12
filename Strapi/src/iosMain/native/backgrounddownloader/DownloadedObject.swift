import Foundation

@objc public class DownloadedObject: NSObject {
    @objc public let url: NSURL?
    @objc public let fileExtension: String
    @objc public let identifier: String
    @objc public let downloadStartTime: NSNumber
    @objc public let downloadEndTime: NSNumber?

    @objc public init(
        url: NSURL?,
        fileExtension: String,
        identifier: String,
        downloadStartTime: NSNumber,
        downloadEndTime: NSNumber?
    ) {
        self.url = url
        self.fileExtension = fileExtension
        self.identifier = identifier
        self.downloadStartTime = downloadStartTime
        self.downloadEndTime = downloadEndTime
    }
}