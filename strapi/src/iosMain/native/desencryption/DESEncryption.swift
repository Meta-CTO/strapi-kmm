import Foundation
import CommonCrypto
import Security

private extension Data {
    func crypt(operation: CCOperation, key: String, iv: String) -> Data? {
        let algorithm = kCCAlgorithmDES
        let options = kCCOptionPKCS7Padding
        let keyData = [UInt8](Data(key.utf8))
        let ivData = [UInt8](Data(iv.utf8))
        let keyLength = kCCKeySizeDES
        let dataIn = [UInt8](self)
        let dataInLength = count
        let dataOutAvailable = dataInLength + kCCBlockSizeDES
        let dataOut = UnsafeMutablePointer<UInt8>.allocate(capacity: dataOutAvailable)
        var dataOutMoved = 0

        let cryptStatus = CCCrypt(
            CCOperation(operation),
            CCAlgorithm(algorithm),
            CCOptions(options),
            keyData,
            keyLength,
            ivData,
            dataIn,
            dataInLength,
            dataOut,
            dataOutAvailable,
            &dataOutMoved
        )

        if cryptStatus == kCCSuccess {
            let result = Data(bytes: dataOut, count: dataOutMoved)
            dataOut.deallocate()
            return result
        } else {
            assertionFailure("\(#function) error = \(cryptStatus)")
            dataOut.deallocate()
            return nil
        }
    }
}

@objc public final class DESEncryption: NSObject {
    private let key: String
    private let iv: String

    @objc public init(key: String, iv: String) {
        self.key = key
        self.iv = iv
    }

    @objc(encryptString:) public func encrypt(string: String) -> String {
        guard
            let data = string.data(using: .utf8)?.crypt(operation: CCOperation(kCCEncrypt), key: key, iv: iv)
        else {
            assertionFailure("Unable to encrypt string")
            return ""
        }

        return data.base64EncodedString()
    }

    @objc(decryptString:) public func decrypt(string: String) -> String {
        let data = Data(base64Encoded: string)

        guard
            let data = data?.crypt(operation: CCOperation(kCCDecrypt), key: key, iv: iv),
            let string = String(data: data, encoding: .utf8)
        else {
            assertionFailure("Unable to decrypt string")
            return ""
        }

        return string
    }
}