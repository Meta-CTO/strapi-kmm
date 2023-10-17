import Foundation

public final class SHCacheManager {
    
    private let queue = DispatchQueue(label: "com.shmediacache.atomic", attributes: .concurrent)
    
    public init() { }
    
    public func purgeCache(for asset: Asset) throws {
        try purgeCache(identifier: asset.identifier)
    }
    
    public func isAssetCached(asset: Asset) -> Bool {
        return pathForCachedAsset(for: asset, purgeInvalidFiles: false) != nil
    }
    
    func purgeCache(identifier: String) throws {
        try deleteCachedFile(identifier: identifier)
        purgeDownloadedAsset(for: identifier)
    }
    
    func assetDidBeginDownload(asset: Asset) {
        let downloadedAsset = DownloadedAsset(from: asset)

        do {
            var cachedDownloadedAssets = try getCachedDownloadedAssets()
            
            if let index = cachedDownloadedAssets.firstIndex(where: { $0.identifier == asset.identifier }) {
                cachedDownloadedAssets[index] = downloadedAsset
            } else {
                cachedDownloadedAssets.append(downloadedAsset)
            }
            
            saveCachedDownloadedAssets(updatedDownloadedAssets: cachedDownloadedAssets)
        } catch {
            SHLogger.log(
                "[SHBackgroundDownloader] ❌ Unable to load cached assets with error \(error)",
                level: .error
            )
        }
    }
    
    func assetDidDownloadSuccessfully(identifier: String) {
        do {
            var cachedDownloadedAssets = try getCachedDownloadedAssets()
            
            if let index = cachedDownloadedAssets.firstIndex(where: { $0.identifier == identifier }) {
                cachedDownloadedAssets[index] = cachedDownloadedAssets[index].update(
                    downloadEndTime: Int(Date().timeIntervalSince1970)
                )
                
                saveCachedDownloadedAssets(updatedDownloadedAssets: cachedDownloadedAssets)
            } else {
                SHLogger.log(
                    "[SHBackgroundDownloader] ⚠️ Unable to locate saved record for asset with identifier \(identifier)",
                    level: .warning
                )
            }
        } catch {
            SHLogger.log(
                "[SHBackgroundDownloader] ❌ Unable to load cached assets with error \(error)",
                level: .error
            )
        }
    }
    
    func assetDidFailToDownload(identifier: String) {
        purgeDownloadedAsset(for: identifier)
    }
    
    func pathForCachedAsset(for asset: Asset, purgeInvalidFiles: Bool) -> URL? {
        do {
            let downloadedAssets = try getCachedDownloadedAssets()
            let cacheURL = try makeCacheURL(for: asset.identifier)
            
            guard
                let downloadedAssetIndex = downloadedAssets.firstIndex(where: { $0.identifier == asset.identifier }),
                FileManager.default.fileExists(atPath: cacheURL.path)
            else {
                if purgeInvalidFiles {
                    try deleteCachedFile(identifier: asset.identifier)
                }
                
                return nil
            }
            
            // Downloaded file is invalid
            if downloadedAssets[downloadedAssetIndex].downloadEndTime == nil && purgeInvalidFiles {
                try purgeCache(for: asset)
                return nil
            }
            
            return cacheURL
        } catch {
            SHLogger.log(
                "[SHBackgroundDownloader] ❌ Unable to get cached asset URL with error \(error)",
                level: .error
            )
            
            return nil
        }
    }
    
    
    func makeCacheURL(for identifier: String) throws -> URL {
        let cacheURL = try FileManager.default.url(
            for: .cachesDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true
        )
        .appendingPathComponent(identifier)
        
        return cacheURL
    }
    
    func getCachedDownloadedAssets() throws -> [DownloadedAsset] {
        try queue.sync {
            let cacheFileURL = try makeCacheURL(for: "caches.txt")
            if !FileManager.default.fileExists(atPath: cacheFileURL.path) {
                let emptyCachedAssets: [DownloadedAsset] = []
                let emptyData = try JSONEncoder().encode(emptyCachedAssets)
                FileManager.default.createFile(atPath: cacheFileURL.path, contents: emptyData)
            }
            
            let data = try Data(contentsOf: cacheFileURL)
            return try JSONDecoder().decode([DownloadedAsset].self, from: data)
        }
    }
    
    private func saveCachedDownloadedAssets(updatedDownloadedAssets: [DownloadedAsset]) {
        queue.async(flags: .barrier) {
            do {
                let cacheFileURL = try self.makeCacheURL(for: "caches.txt")
                let data = try JSONEncoder().encode(updatedDownloadedAssets)
                
                try data.write(to: cacheFileURL)
            } catch {
                
            }
        }
    }
    
    private func purgeDownloadedAsset(for identifier: String) {
        do {
            var cachedDownloadedAssets = try getCachedDownloadedAssets()
            
            if let index = cachedDownloadedAssets.firstIndex(where: { $0.identifier == identifier }) {
                cachedDownloadedAssets.remove(at: index)
                saveCachedDownloadedAssets(updatedDownloadedAssets: cachedDownloadedAssets)
            } else {
                SHLogger.log(
                    "[SHBackgroundDownloader] ⚠️ Unable to locate saved record for asset with identifier \(identifier)",
                    level: .warning
                )
            }
        } catch {
            SHLogger.log(
                "[SHBackgroundDownloader] ❌ Unable to load cached assets with error \(error)",
                level: .error
            )
        }
    }
    
    private func deleteCachedFile(identifier: String) throws {
        let cacheURL = try makeCacheURL(for: identifier)
        if FileManager.default.fileExists(atPath: cacheURL.path) {
            try FileManager.default.removeItem(atPath: cacheURL.path)
        }
    }
}
