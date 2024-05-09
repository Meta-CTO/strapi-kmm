import Foundation
import zlib

/// Errors on gzipping/gunzipping based on the zlib error codes.
public struct GzipError: Swift.Error, Sendable {
    // cf. http://www.zlib.net/manual.html
    
    public enum Kind: Equatable, Sendable {
        /// The stream structure was inconsistent.
        ///
        /// - underlying zlib error: `Z_STREAM_ERROR` (-2)
        case stream
        
        /// The input data was corrupted
        /// (input stream not conforming to the zlib format or incorrect check value).
        ///
        /// - underlying zlib error: `Z_DATA_ERROR` (-3)
        case data
        
        /// There was not enough memory.
        ///
        /// - underlying zlib error: `Z_MEM_ERROR` (-4)
        case memory
        
        /// No progress is possible or there was not enough room in the output buffer.
        ///
        /// - underlying zlib error: `Z_BUF_ERROR` (-5)
        case buffer
        
        /// The zlib library version is incompatible with the version assumed by the caller.
        ///
        /// - underlying zlib error: `Z_VERSION_ERROR` (-6)
        case version
        
        /// An unknown error occurred.
        ///
        /// - parameter code: return error by zlib
        case unknown(code: Int)
    }
    
    /// Error kind.
    public let kind: Kind
    
    /// Returned message by zlib.
    public let message: String
    
    
    internal init(code: Int32, msg: UnsafePointer<CChar>?) {
        message = msg.flatMap(String.init(validatingUTF8:)) ?? "Unknown gzip error"
        kind = Kind(code: code)
    }

    internal init(code: Int32, msg: String) {
       message = msg
       kind = Kind(code: code)
    }
    
    public var localizedDescription: String {
        return message
    }
}


private extension GzipError.Kind {
    init(code: Int32) {
        switch code {
        case Z_STREAM_ERROR:
            self = .stream
        case Z_DATA_ERROR:
            self = .data
        case Z_MEM_ERROR:
            self = .memory
        case Z_BUF_ERROR:
            self = .buffer
        case Z_VERSION_ERROR:
            self = .version
        default:
            self = .unknown(code: Int(code))
        }
    }
}

private enum DataSize {
    static let chunk = 1 << 14
    static let stream = MemoryLayout<z_stream>.size
}

@objc public class GZipDecompressor: NSObject {
    @objc public override init() {

    }

    @objc(gunzipString:wBits:error:) public func gunzip(input: String, wBits: Int32) throws -> String {
            guard let compressed = Data(base64Encoded: input) else {
                throw GzipError(code: -1001, msg: "Unable to decode base64 encoded string")
            }

            var data = Data(capacity: compressed.count * 2)
            var totalIn: uLong = 0
            var totalOut: uLong = 0
            
            repeat {
                var stream = z_stream()
                var status: Int32
                
                status = inflateInit2_(&stream, wBits, ZLIB_VERSION, Int32(DataSize.stream))
                
                guard status == Z_OK else {
                    // inflateInit2 returns:
                    // Z_VERSION_ERROR   The zlib library version is incompatible with the version assumed by the caller.
                    // Z_MEM_ERROR       There was not enough memory.
                    // Z_STREAM_ERROR    A parameters are invalid.
                    throw GzipError(code: status, msg: stream.msg)
                }
                
                repeat {
                    if Int(totalOut + stream.total_out) >= data.count {
                        data.count += compressed.count / 2
                    }
                    
                    let inputCount = compressed.count
                    let outputCount = data.count
                    
                    compressed.withUnsafeBytes { inputPointer in
                        let inputStartPosition = totalIn + stream.total_in

                        guard
                            let baseAddress = inputPointer.bindMemory(to: Bytef.self).baseAddress
                        else {
                            assertionFailure("Base address for inputPointer not found")
                            return
                        }

                        stream.next_in = UnsafeMutablePointer(mutating: baseAddress).advanced(by: Int(inputStartPosition))
                        stream.avail_in = uInt(inputCount) - uInt(inputStartPosition)
                        
                        data.withUnsafeMutableBytes { outputPointer in
                            let outputStartPosition = totalOut + stream.total_out

                            guard
                                let baseAddress = outputPointer.bindMemory(to: Bytef.self).baseAddress
                            else {
                                assertionFailure("Base address for outputPointer not found")
                                return
                            }

                            stream.next_out = baseAddress.advanced(by: Int(outputStartPosition))
                            stream.avail_out = uInt(outputCount) - uInt(outputStartPosition)
                            status = inflate(&stream, Z_SYNC_FLUSH)
                            stream.next_out = nil
                        }
                        
                        stream.next_in = nil
                    }
                } while (status == Z_OK)
                
                totalIn += stream.total_in
                
                guard inflateEnd(&stream) == Z_OK, status == Z_STREAM_END else {
                    // inflate returns:
                    // Z_DATA_ERROR   The input data was corrupted (input stream not conforming to the zlib format or incorrect check value).
                    // Z_STREAM_ERROR The stream structure was inconsistent (for example if next_in or next_out was NULL).
                    // Z_MEM_ERROR    There was not enough memory.
                    // Z_BUF_ERROR    No progress is possible or there was not enough room in the output buffer when Z_FINISH is used.
                    throw GzipError(code: status, msg: stream.msg)
                }
                
                totalOut += stream.total_out
                
            } while (totalIn < compressed.count)
            
            data.count = Int(totalOut)

            guard let string = String(data: data, encoding: .utf8) else {
                throw GzipError(code: -1002, msg: "Unable to decode unzipped data")
            }

            return string
        }
}
