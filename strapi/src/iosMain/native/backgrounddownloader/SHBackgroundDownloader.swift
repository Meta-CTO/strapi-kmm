import Foundation

@objc public protocol SHBackgroundDownloaderDelegate: AnyObject {
    @objc(downloaderDidStartDownloadingAsset:)
    func downloaderDidStartDownloadingAsset(id: String)

    @objc(downloaderDidDownloadAssetWithIdentifier:url:)
    func downloaderDidDownloadAssetWithIdentifierAndURL(id: String, url: URL)

    @objc(downloaderDidFailToDownloadAssetWithIdentifier:errorCode:)
    func downloaderDidFailToDownloadAssetWithIdentifierAndErrorCode(id: String, errorCode: Int)

    @objc(downloaderDidUpdateProgress:id:)
    func downloaderDidUpdateProgressForAssetWithIdentifier(progress: Double, id: String)

    @objc(downloaderDidFailToResumeUnfinishedDownloadsWithError:)
    func downloaderDidFailToResumeUnfinishedDownloads(error: Error)

    @objc(downloaderDidResumeUnfinishedDownloadWithIdentifier:)
    func downloaderDidResumeUnfinishedDownloadWithIdentifier(id: String)
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
    
    @objc(downloadURL:error:) public func download(url: URL) throws {
        if !Self.allowsCellularDownloads && PathMonitor.shared.isExpensive {
            throw DownloaderError.cellularDownloadNotAllowed
        }

        download(asset: url)
    }
    
    @objc(downloadURLs:error:) public func download(urls: [URL]) throws {
        if !Self.allowsCellularDownloads && PathMonitor.shared.isExpensive {
            throw DownloaderError.cellularDownloadNotAllowed
        }

        download(assets: urls)
    }

    @objc public func resumeUnfinishedDownloads() {
        do {
            let partiallyDownloadedAssets = try cacheManager.getCachedDownloadedAssets().filter { $0.downloadEndTime == nil }
            download(assets: partiallyDownloadedAssets)
        } catch {
            delegate?.downloaderDidFailToResumeUnfinishedDownloads(error: error)
        }
    }
    
    private func download(asset: Asset) {
        if let path = cacheManager.pathForCachedAsset(for: asset, purgeInvalidFiles: false) {
            delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: asset.identifier, url: path)
        } else {
            queue.enqueue(asset)
            processQueue()
        }
    }
    
    private func download(assets: [Asset]) {
        for asset in assets {
            if let path = cacheManager.pathForCachedAsset(for: asset, purgeInvalidFiles: false) {
                delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: asset.identifier, url: path)
                continue
            }
            
            queue.enqueue(asset)
        }
        
        processQueue()
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
        guard
            let url = asset.url
        else {
            assertionFailure("Unsupported configuration, URL is null")
            return
        }
        
        let task = session.downloadTask(with: url)
        task.taskDescription = asset.identifier
        task.resume()
        numberOfRunningTasks += 1
        cacheManager.assetDidBeginDownload(asset: asset)

        delegate?.downloaderDidStartDownloadingAsset(id: asset.identifier)
    }

    private enum DownloaderError: LocalizedError {
        case cellularDownloadNotAllowed

        var errorDescription: String? {
            switch self {
            case .cellularDownloadNotAllowed:
                return "Cellular download not allowed"
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
        
        guard
            let response = downloadTask.response as? HTTPURLResponse
        else {
            SHLogger.log("[SHBackgroundDownloader] ⚠️ Unable to cast response to HTTPURLResponse", level: .warning)
            return
        }
        
        if (200...299).contains(response.statusCode) {
            do {
                let cacheURL = try cacheManager.makeCacheURL(for: assetIdentifier)
                try FileManager.default.copyItem(at: location, to: cacheURL)

                delegate?.downloaderDidDownloadAssetWithIdentifierAndURL(id: assetIdentifier, url: cacheURL)
                
                cacheManager.assetDidDownloadSuccessfully(identifier: assetIdentifier)
            } catch {
                delegate?.downloaderDidFailToDownloadAssetWithIdentifierAndErrorCode(
                    id: assetIdentifier,
                    errorCode: (error as NSError).code
                 )
                
                cacheManager.assetDidFailToDownload(identifier: assetIdentifier)
            }
        } else {
            delegate?.downloaderDidFailToDownloadAssetWithIdentifierAndErrorCode(
                id: assetIdentifier,
                errorCode: response.statusCode
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
        delegate?.downloaderDidUpdateProgressForAssetWithIdentifier(progress: progress, id: assetIdentifier)
    }

    public func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        guard let data = (error as? NSError)?.userInfo[NSURLSessionDownloadTaskResumeData] as? Data else { return }
        let resumeTask = session.downloadTask(withResumeData: data)
        resumeTask.taskDescription = task.taskDescription
        resumeTask.resume()
        numberOfRunningTasks += 1
        delegate?.downloaderDidResumeUnfinishedDownloadWithIdentifier(id: task.taskDescription ?? "")
    }
}
