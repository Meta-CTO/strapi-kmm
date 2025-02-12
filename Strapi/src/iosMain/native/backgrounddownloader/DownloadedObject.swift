import Foundation

@objc public class DownloadedObject: NSObject {
    public let url: URL?
    public let fileExtension: String
    public let identifier: String
    public let downloadStartTime: Int
    public let downloadEndTime: Int?

    init(
        url: URL?,
        fileExtension: String,
        identifier: String,
        downloadStartTime: Int,
        downloadEndTime: Int?
    ) {
        self.url = url
        self.fileExtension = fileExtension
        self.identifier = identifier
        self.downloadStartTime = downloadStartTime
        self.downloadEndTime = downloadEndTime
    }
}
