package com.swensonhe.strapikmm.contact

import com.swensonhe.strapikmm.common.contacts.contactsdatacollector.SHContactsDataCollector
import com.swensonhe.strapikmm.model.contact.Contact
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import platform.Contacts.CNContact
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNPhoneNumber
import kotlin.coroutines.resumeWithException
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

/**
 * iOS platform-specific implementation of a contacts data collector that retrieves contacts information
 * from the device's address book on iOS.
 */
@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class ContactsDataCollector {

    // Initialize the swift contacts data collector.
    private val contactsDataCollector = SHContactsDataCollector.shared()

    /**
     * Requests access to the device's address book.
     *
     * @return `true` if access is granted, `false` otherwise.
     * @throws Throwable if there is an error during the access request.
     */
    @Throws(Throwable::class)
    actual suspend fun requestAccess(): Boolean = suspendCancellableCoroutine { continuation ->
        contactsDataCollector.requestAccess { isGranted, error ->
            if (continuation.isActive.not()) return@requestAccess

            if (error != null) {
                continuation.resumeWithException(Throwable(error.localizedDescription))
            } else {
                continuation.resume(isGranted)
            }
        }
    }

    /**
     * Loads the list of contacts from the device's address book.
     *
     * @return A list of [Contact] objects representing the retrieved contacts.
     * @throws Throwable if there is an error during the contact loading process.
     */
    @Throws(Throwable::class)
    actual suspend fun loadContacts(): List<Contact> = suspendCancellableCoroutine { continuation ->
        contactsDataCollector.loadContacts { contacts, error ->
            if (continuation.isActive.not()) return@loadContacts

            if (error != null) {
                continuation.resumeWithException(Throwable(error.localizedDescription))
            } else {
                // Convert the contacts to a list of Contact objects.
                val contacts = contacts?.map { contact ->
                    // Convert the CNContact to a Contact object.
                    val cnContact = contact as? CNContact
                    // Get the phone numbers from the CNContact.
                    val phoneNumbers = cnContact?.phoneNumbers
                        ?.map { it as? CNLabeledValue }
                        ?.map { (it?.value as? CNPhoneNumber)?.stringValue ?: "" }
                        ?.filter { it.isNotEmpty() }
                    // Get the email addresses from the CNContact.
                    val emailAddresses = cnContact?.emailAddresses as? List<CNLabeledValue>
                    // Get the postal addresses from the CNContact.
                    val postalAddresses = cnContact?.postalAddresses as? List<CNLabeledValue>
                    // Get the URL addresses from the CNContact.
                    val urlAddresses = cnContact?.urlAddresses as? List<CNLabeledValue>
                    // Get the instant message addresses from the CNContact.
                    val instantMessageAddresses = cnContact?.instantMessageAddresses as? List<CNLabeledValue>
                    // Get the social profiles from the CNContact.
                    val socialProfiles = cnContact?.socialProfiles as? List<CNLabeledValue>
                    // Get the relations from the CNContact.
                    val relations = cnContact?.contactRelations as? List<CNLabeledValue>

                    Contact(
                        cnContact?.givenName,
                        cnContact?.familyName,
                        phoneNumbers ?: emptyList(),
                        emailAddresses?.map { it.value.toString() } ?: emptyList(),
                        cnContact?.middleName,
                        cnContact?.namePrefix,
                        cnContact?.nameSuffix,
                        cnContact?.birthday?.date?.toString(),
                        cnContact?.previousFamilyName,
                        cnContact?.nickname,
                        postalAddresses?.map { it.value.toString() } ?: emptyList(),
                        urlAddresses?.map { it.value.toString() } ?: emptyList(),
                        instantMessageAddresses?.map { it.value.toString() } ?: emptyList(),
                        socialProfiles?.map { it.value.toString() } ?: emptyList(),
                        relations?.map { it.value.toString() } ?: emptyList(),
                        cnContact?.contactType?.name,
                        cnContact?.jobTitle,
                        cnContact?.departmentName,
                        cnContact?.organizationName,
                        cnContact?.imageDataAvailable ?: false,
                        cnContact?.imageData?.toByteArray(),
                        cnContact?.thumbnailImageData?.toByteArray()
                    )
                } ?: emptyList()

                continuation.resume(contacts)
            }
        }
    }

    /**
     * Converts an Objective-C [NSData] object to a Kotlin [ByteArray].
     *
     * @return The equivalent Kotlin byte array.
     */
    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(length.toInt()).apply {
            // Pin the byte array in memory to prevent the garbage collector from moving it to make sure that the position in memory is guaranteed to be stable
            usePinned {
                // Copy the bytes from the NSData object to the pinned byte array.
                memcpy(it.addressOf(0), bytes, length)
            }
        }
    }

    /**
     * Sets options for the contacts data collector.
     *
     * @param options The options to set for the data collector.
     */
    @Throws(Throwable::class)
    actual fun setOptions(options: ContactsDataCollectorOptions?) {
        // no-op on iOS
    }
}

/**
 * Converts a Kotlin [ByteArray] to an equivalent Objective-C [NSData] object using the Swift's `NSData` interoperability.
 *
 * @return An [NSData] object representing the same binary data as the original byte array.
 */
@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData = memScoped {
    // Create an NSData object by allocating memory for the byte array and specifying its length.
    NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
}