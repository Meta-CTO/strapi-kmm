import Foundation

public typealias DownloadIdentifier = String

@objc public protocol SHBackgroundDownloaderDelegate: AnyObject {
    @objc(downloaderDidStartDownloadingAsset:url:downloadURL:)
    func downloaderDidStartDownloadingAsset(id: String, url: URL?, downloadURL: URL?)

    @objc(downloaderDidDownloadAssetWithIdentifier:url:downloadURL:)
    func downloaderDidDownloadAssetWithIdentifierAndURL(id: String, url: URL?, downloadURL: URL?)

    @objc(downloaderDidFailToDownloadAssetWithIdentifier:url:downloadURL:error:)
    func downloaderDidFailToDownloadAssetWithIdentifierAndError(id: String, url: URL?, downloadURL: URL?, error: Error)

    @objc(downloaderDidUpdateProgress:url:downloadURL:progress:)
    func downloaderDidUpdateProgressForAssetWithIdentifier(id: String, url: URL?, downloadURL: URL?, progress: Double)

    @objc(downloaderDidFailToResumeUnfinishedDownloadsWithError:)
    func downloaderDidFailToResumeUnfinishedDownloads(error: Error)

    @objc(downloaderDidResumeUnfinishedDownloadWithIdentifier:url:downloadURL:)
    func downloaderDidResumeUnfinishedDownloadWithIdentifier(id: String, url: URL?, downloadURL: URL?)
}

@objc public final class SHBackgroundDownloader: NSObject {
    
    // MARK: Singleton
    @objc public static let shared = SHBackgroundDownloader()
    
    private override init() {
        super.init()
    }
    
    // MARK: Configuration
    public private(set) static var maximumNumberOfConcurrentDownloads = 5
    public private(set) static var allowsCellularDownloads: Bool = false
    
    // MARK: Dependencies
    private let cacheManager = SHCacheManager()
    
    // MARK: Download Management
    @objc public weak var delegate: SHBackgroundDownloaderDelegate?
    private var queue: Queue<Asset> = .init()
    private var numberOfRunningTasks = 0
    @Atomic private var currentDownloadingAssets: [Asset] = []
    
    @objc(configureMaximumNumberConcurrentDownloads:allowsCellularDownloads:)
    public static func configure(maximumNumberOfConcurrentDownloads: Int, allowsCellularDownloads: Bool) {
        Self.maximumNumberOfConcurrentDownloads = maximumNumberOfConcurrentDownloads
        Self.allowsCellularDownloads = allowsCellularDownloads
    }
    
    private lazy var session: URLSession = {
        let config = URLSessionConfiguration.background(withIdentifier: "com.shmediacache.downloadqueue")
        return URLSession(configuration: config, delegate: self, delegateQueue: nil)
    }()


    @objc(downloadURL:error:) public func download(url: URL) throws -> DownloadIdentifier {
        if !Self.allowsCellularDownloads && PathMonitor.shared.isExpensive {
            throw DownloaderError.cellularDownloadNotAllowed
        }

        return download(asset: url)
    }
    
    @objc(downloadURLs:error:) public func download(urls: [URL]) throws -> [DownloadIdentifier] {
        if !Self.allowsCellularDownloads && PathMonitor.shared.isExpensive {
            throw DownloaderError.cellularDownloadNotAllowed
        }

        return download(assets: urls)
    }

    @objc(checkDownloadStatusWithURL:error:)
    public func checkDownloadStatus(url: URL) throws -> DownloadedObject {
        let asset: Asset = url

        let cachedAssets: [DownloadedAsset]
        do {
            cachedAssets = try cacheManager.getCachedDownloadedAssets()
        } catch {
            throw DownloaderError.notDownloaded // Convert any error to notDownloaded
        }

        guard let downloadedAsset = cachedAssets.first(where: { $0.identifier == asset.identifier }) else {
            throw DownloaderError.notDownloaded
        }

        return DownloadedObject(
            url: downloadedAsset.url,
            fileExtension: downloadedAsset.fileExtension,
            identifier: downloadedAsset.identifier,
            downloadStartTime: downloadedAsset.downloadStartTime,
            downloadEndTime: downloadedAsset.downloadEndTime
        )
    }

    @objc public func resumeUnfinishedDownloads() {
        do {
            let partiallyDownloadedAssets = try cacheManager.getCachedDownloadedAssets().filter { $0.downloadEndTime == nil }
            _ = download(assets: partiallyDownloadedAssets)
        } catch {
            delegate?.downloaderDidFailToResumeUnfinishedDownloads(error: error)
        }
    }

    private func download(asset: Asset) -> String {
        if let path = cacheManager.pathForCachedAsset(for: asset, purgeInvalidFiles: false) {
            let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == asset.identifier }?.url
            delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: asset.identifier, url: path, downloadURL: downloadURL)
        } else {
            queue.enqueue(asset)
            processQueue()
        }

        return asset.identifier
    }

    private func download(assets: [Asset]) -> [String] {
        for asset in assets {
            if let path = cacheManager.pathForCachedAsset(for: asset, purgeInvalidFiles: false) {
                let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == asset.identifier }?.url
                delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: asset.identifier, url: path, downloadURL: downloadURL)
                continue
            }

            queue.enqueue(asset)
        }

        processQueue()

        return assets.map(\.identifier)
    }
    
    private func isAssetQueuedForDownload(asset: Asset) -> Bool {
        return queue.elements.contains(where: { $0.identifier == asset.identifier }) ||
            currentDownloadingAssets.contains(where: { $0.identifier == asset.identifier })
    }
    
    private func processQueue() {
        guard !queue.elements.isEmpty else { return }

        let numberOfAssetsToDequeue = Self.maximumNumberOfConcurrentDownloads - numberOfRunningTasks

        Task {
            let tasks = await session.allTasks

            for _ in 0..<numberOfAssetsToDequeue {
                if let asset = queue.dequeue(),
                    !tasks.contains(where: { $0.taskDescription == asset.identifier }),
                    !isAssetQueuedForDownload(asset: asset) {

                    // Don't enqueue tasks when on cellular
                    if !Self.allowsCellularDownloads && PathMonitor.shared.isExpensive {
                        continue
                    }

                    enqueueTask(for: asset)
                    currentDownloadingAssets.append(asset)
                }
            }
        }
    }

    private func enqueueTask(for asset: Asset) {
        guard let url = asset.url else {
            assertionFailure("Unsupported configuration, URL is null")
            return
        }

        let task = session.downloadTask(with: url)
        task.taskDescription = asset.identifier
        task.resume()
        numberOfRunningTasks += 1
        cacheManager.assetDidBeginDownload(asset: asset)

        let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == asset.identifier }?.url

        delegate?.downloaderDidStartDownloadingAsset(id: asset.identifier, url: asset.url, downloadURL: downloadURL)
    }

    private enum DownloaderError: LocalizedError {
        case cellularDownloadNotAllowed
        case notDownloaded

        var errorDescription: String? {
            switch self {
            case .cellularDownloadNotAllowed:
                return "Cellular download not allowed"
            case .notDownloaded:
                return "Asset not downloaded"
            }
        }
    }
}

extension SHBackgroundDownloader: URLSessionDelegate {
    
}

extension SHBackgroundDownloader: URLSessionDownloadDelegate {

    public func urlSession(
        _ session: URLSession,
        downloadTask: URLSessionDownloadTask,
        didFinishDownloadingTo location: URL
    ) {
        numberOfRunningTasks -= 1

        let assetIdentifier = downloadTask.taskDescription ?? ""
        currentDownloadingAssets.removeAll(where: { $0.identifier == assetIdentifier })

        guard let response = downloadTask.response as? HTTPURLResponse else {
            SHLogger.log("[SHBackgroundDownloader] ⚠️ Unable to cast response to HTTPURLResponse", level: .warning)
            return
        }

        let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == assetIdentifier }?.url

        if (200...299).contains(response.statusCode) {
            do {
                let cacheURL = try cacheManager.makeCacheURL(for: assetIdentifier)
                try FileManager.default.copyItem(at: location, to: cacheURL)

                delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: assetIdentifier, url: cacheURL, downloadURL: downloadURL)

                cacheManager.assetDidDownloadSuccessfully(identifier: assetIdentifier)
            } catch {
                delegate?.downloaderDidFailToDownloadAssetWithIdentifierAndError(
                    id: assetIdentifier, url: response.url, downloadURL: downloadURL, error: error
                )
                cacheManager.assetDidFailToDownload(identifier: assetIdentifier)
            }
        } else {
            delegate?.downloaderDidFailToDownloadAssetWithIdentifierAndError(
                id: assetIdentifier,
                url: response.url,
                downloadURL: downloadURL,
                error: NSError(
                    domain: "SHBackgroundDownloader",
                    code: response.statusCode,
                    userInfo: [NSLocalizedDescriptionKey: "Unable to download asset due to network error"]
                )
            )

            cacheManager.assetDidFailToDownload(identifier: assetIdentifier)
        }
        
        processQueue()
    }
    
    public func urlSession(
        _ session: URLSession,
        downloadTask: URLSessionDownloadTask,
        didWriteData bytesWritten: Int64,
        totalBytesWritten: Int64,
        totalBytesExpectedToWrite: Int64
    ) {
        guard
            totalBytesExpectedToWrite != NSURLSessionTransferSizeUnknown
        else {
            return
        }

        let assetIdentifier = downloadTask.taskDescription ?? ""
        let progress = Double(totalBytesWritten) / Double(totalBytesExpectedToWrite)
        let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == assetIdentifier }?.url
        delegate?.downloaderDidUpdateProgressForAssetWithIdentifier(
            id: assetIdentifier,
            url: downloadTask.originalRequest?.url,
            downloadURL: downloadURL,
            progress: progress
        )
    }

    public func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        guard let data = (error as? NSError)?.userInfo[NSURLSessionDownloadTaskResumeData] as? Data else { return }
        let resumeTask = session.downloadTask(withResumeData: data)
        resumeTask.taskDescription = task.taskDescription
        resumeTask.resume()
        numberOfRunningTasks += 1
        let downloadURL = try? cacheManager.getCachedDownloadedAssets().first { $0.identifier == task.taskDescription ?? "" }?.url
        delegate?.downloaderDidResumeUnfinishedDownloadWithIdentifier(
            id: task.taskDescription ?? "",
            url: task.originalRequest?.url,
            downloadURL: downloadURL
        )
    }
}
