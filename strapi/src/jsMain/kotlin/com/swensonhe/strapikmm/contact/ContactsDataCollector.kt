package com.swensonhe.strapikmm.contact

import com.swensonhe.strapikmm.model.contact.Contact

/**
 *  Provide contacts collecting for web
 * We didn't implement any logic for Web (For now), so we just return a no-op implementation here.
 * ** Any PRs to implement it for Web are welcome! **
 */
actual class ContactsDataCollector {
    actual fun setOptions(options: ContactsDataCollectorOptions?) {
        // no-op in JS
    }

    actual suspend fun requestAccess(): Boolean {
        // no-op in JS
        throw Throwable("Not implemented")
    }

    actual suspend fun loadContacts(): List<Contact> {
        // no-op in JS
        throw Throwable("Not implemented")
    }
}