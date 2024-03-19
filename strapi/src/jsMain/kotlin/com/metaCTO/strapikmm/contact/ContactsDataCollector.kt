package com.metaCTO.strapikmm.contact

import com.metaCTO.strapikmm.model.contact.Contact

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