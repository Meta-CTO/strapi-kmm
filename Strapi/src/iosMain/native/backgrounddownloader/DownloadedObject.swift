import Foundation

@objc public class DownloadedObject: NSObject {
    let url: URL?
    let fileExtension: String
    let identifier: String
    let downloadStartTime: Int
    let downloadEndTime: Int?

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
