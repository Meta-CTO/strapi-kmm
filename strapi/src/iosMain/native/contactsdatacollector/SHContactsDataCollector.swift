import Foundation
import Contacts

@objc public final class SHContactsDataCollector: NSObject {

    // MARK: Singleton
    @objc public static let shared = SHContactsDataCollector()

    private override init() {
        super.init()
    }

    // MARK: Dependencies
    private let store = CNContactStore()

    // MARK: Public methods
    @objc(requestAccess:) public func requestAccess() async throws -> Bool {
        do {
            return try await store.requestAccess(for: .contacts)
        } catch {
            throw error
        }
    }

    @objc(loadContacts:) public func loadContacts() async throws -> [CNContact] {
        try await withCheckedThrowingContinuation { continuation in
            loadContacts { result in
                switch result {
                case .success(let contacts):
                    continuation.resume(returning: contacts)

                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    private func loadContacts(completionHandler: @escaping (Result<[CNContact], Error>) -> Void) {
        DispatchQueue.global().async {
            var contacts = [CNContact]()

            let keys = [
                CNContactNamePrefixKey,
                CNContactMiddleNameKey,
                CNContactGivenNameKey,
                CNContactFamilyNameKey,
                CNContactPreviousFamilyNameKey,
                CNContactNameSuffixKey,
                CNContactNicknameKey,
                CNContactOrganizationNameKey,
                CNContactDepartmentNameKey,
                CNContactJobTitleKey,
                CNContactPhoneticGivenNameKey,
                CNContactPhoneticMiddleNameKey,
                CNContactPhoneticFamilyNameKey,
                CNContactPhoneticOrganizationNameKey,
                CNContactBirthdayKey,
                CNContactImageDataKey,
                CNContactThumbnailImageDataKey,
                CNContactImageDataAvailableKey,
                CNContactPhoneNumbersKey,
                CNContactEmailAddressesKey,
                CNContactPostalAddressesKey,
                CNContactTypeKey,
                CNContactUrlAddressesKey,
                CNContactSocialProfilesKey,
                CNContactRelationsKey,
                CNContactInstantMessageAddressesKey
            ]

            let request = CNContactFetchRequest(
                keysToFetch: keys as [CNKeyDescriptor]
            )

            do {
                try self.store.enumerateContacts(with: request) { contact, _  in
                    contacts.append(contact)
                }

                completionHandler(.success(contacts))
            } catch {
                completionHandler(.failure(error))
            }
        }
    }

}
