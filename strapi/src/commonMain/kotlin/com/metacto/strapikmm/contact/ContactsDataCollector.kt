package com.metacto.strapikmm.contact

import com.metacto.strapikmm.model.contact.Contact

expect class ContactsDataCollector {
    @Throws(Throwable::class)
    fun setOptions(options: ContactsDataCollectorOptions?)

    @Throws(Throwable::class)
    suspend fun requestAccess(): Boolean

    @Throws(Throwable::class)
    suspend fun loadContacts(): List<Contact>
}