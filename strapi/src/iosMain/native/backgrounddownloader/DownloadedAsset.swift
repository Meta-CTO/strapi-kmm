import Foundation

struct DownloadedAsset: Codable, Asset {
    let url: URL?
    let fileExtension: String
    let identifier: String
    let downloadStartTime: Int
    let downloadEndTime: Int?

    func update(
        url: URL? = nil,
        fileExtension: String? = nil,
        identifier: String? = nil,
        downloadStartTime: Int? = nil,
        downloadEndTime: Int? = nil
    ) -> Self {
        return .init(
            url: url ?? self.url,
            fileExtension: fileExtension ?? self.fileExtension,
            identifier: identifier ?? self.identifier,
            downloadStartTime: downloadStartTime ?? self.downloadStartTime,
            downloadEndTime: downloadEndTime ?? self.downloadEndTime
        )
    }
}

extension DownloadedAsset {
    init(from asset: Asset) {
        self.init(
            url: asset.url,
            fileExtension: asset.fileExtension,
            identifier: asset.identifier,
            downloadStartTime: Int(Date().timeIntervalSince1970),
            downloadEndTime: nil
        )
    }
}

