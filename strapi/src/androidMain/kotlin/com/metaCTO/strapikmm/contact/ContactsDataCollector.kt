package com.metaCTO.strapikmm.contact

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactStore
import com.metaCTO.strapikmm.util.exceptionIfActive
import com.metaCTO.strapikmm.util.resumeIfActive
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.*

actual class ContactsDataCollector {
    private lateinit var options: ContactsDataCollectorOptions
    private var requestAccessCont: CancellableContinuation<Boolean>? = null
    private val contactsStore by lazy {
        ContactStore.newInstance(options.context.applicationContext)
    }

    @Throws(Throwable::class)
    actual fun setOptions(options: ContactsDataCollectorOptions?) {
        // Validate options, must not be null
        requireNotNull(options) {
            "options cannot be null"
        }

        // Set options and result handler
        this.options = options
        options.onResult = ::handlePermissionResult
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        // Resume the continuation if possible
        requestAccessCont?.resumeIfActive(isGranted)
    }

    @Throws(Throwable::class)
    actual suspend fun requestAccess(): Boolean = suspendCancellableCoroutine { cont ->
        // Check if contacts permission is granted
        if (isContactsPermissionGranted()) {
            // Granted, resume the continuation if possible
            cont.resumeIfActive(true)
        } else {
            // Not granted, launch contacts permission requester from the user
            requestAccessCont = cont
            options.launcher.launch(CONTACTS_PERMISSION)
        }
    }

    @Throws(Throwable::class)
    actual suspend fun loadContacts() = suspendCancellableCoroutine { cont ->
        // Validate contacts permission
        if (isContactsPermissionGranted().not()) {
            // Contacts permission must bet granted first
            cont.exceptionIfActive(
                Throwable("Contacts permission is not granted, use requestAccess() fun to be granted.")
            )
            return@suspendCancellableCoroutine
        }

        // Contacts permission is granted,
        // Load contacts
        val contacts = contactsStore.fetchContacts(
            columnsToFetch = CONTACT_COLUMNS
        ).blockingGet()

        // Map them to kmm list and resume the continuation
        val kmmContacts = contacts.toKmmContactsList(
            context = options.context
        )
        cont.resumeIfActive(kmmContacts)
    }


    private fun isContactsPermissionGranted(): Boolean {
        // Check contacts permission
        return ContextCompat.checkSelfPermission(
            options.context,
            CONTACTS_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS
        private val CONTACT_COLUMNS = listOf(
            ContactColumn.Events,
            ContactColumn.Phones,
            ContactColumn.Image,
            ContactColumn.Names,
            ContactColumn.Nickname,
            ContactColumn.Relations,
            ContactColumn.GroupMemberships,
            ContactColumn.CustomDataItems,
            ContactColumn.Organization,
            ContactColumn.PostalAddresses,
            ContactColumn.Note,
            ContactColumn.Mails,
            ContactColumn.ImAddresses,
            ContactColumn.WebAddresses,
            ContactColumn.Organization
        )
    }
}