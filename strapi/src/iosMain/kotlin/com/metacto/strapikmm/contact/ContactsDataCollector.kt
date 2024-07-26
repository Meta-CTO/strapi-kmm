package com.metacto.strapikmm.contact

import com.metacto.strapikmm.common.contacts.contactsdatacollector.SHContactsDataCollector
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.model.contact.Contact
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

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class ContactsDataCollector {

    private val contactsDataCollector = SHContactsDataCollector.shared()

    @Throws(Throwable::class)
    actual suspend fun requestAccess(): Boolean = executeCatching {
        suspendCancellableCoroutine { continuation ->
            contactsDataCollector.requestAccess { isGranted, error ->
                if (continuation.isActive.not()) return@requestAccess

                if (error != null) {
                    continuation.resumeWithException(Throwable(error.localizedDescription))
                } else {
                    continuation.resume(isGranted)
                }
            }
        }
    }

    @Throws(Throwable::class)
    actual suspend fun loadContacts(): List<Contact> = executeCatching {
        suspendCancellableCoroutine { continuation ->
            contactsDataCollector.loadContacts { contacts, error ->
                if (continuation.isActive.not()) return@loadContacts

                if (error != null) {
                    continuation.resumeWithException(Throwable(error.localizedDescription))
                } else {
                    val contacts = contacts?.map { contact ->
                        val cnContact = contact as? CNContact
                        val phoneNumbers = cnContact?.phoneNumbers
                            ?.map { it as? CNLabeledValue }
                            ?.map { (it?.value as? CNPhoneNumber)?.stringValue ?: "" }
                            ?.filter { it.isNotEmpty() }
                        val emailAddresses = cnContact?.emailAddresses as? List<CNLabeledValue>
                        val postalAddresses = cnContact?.postalAddresses as? List<CNLabeledValue>
                        val urlAddresses = cnContact?.urlAddresses as? List<CNLabeledValue>
                        val instantMessageAddresses =
                            cnContact?.instantMessageAddresses as? List<CNLabeledValue>
                        val socialProfiles = cnContact?.socialProfiles as? List<CNLabeledValue>
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
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), bytes, length)
            }
        }
    }

    @Throws(Throwable::class)
    actual fun setOptions(options: ContactsDataCollectorOptions?) {
        // no-op
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
}