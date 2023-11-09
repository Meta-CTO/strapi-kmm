package com.swensonhe.strapikmm.contact

import com.swensonhe.strapikmm.model.contact.Contact

/**
 * An expect class that provides functionality for collecting and managing contact information.
 * Platform-specific implementations of this class should be provided for each platform (e.g., Web, Android, iOS).
 */
expect class ContactsDataCollector {
    /**
     * Sets configuration options for the contact data collection.
     *
     * @param options An instance of [ContactsDataCollectorOptions] containing configuration settings.
     *
     * @throws Throwable if there is an error while setting the options.
     */
    @Throws(Throwable::class)
    fun setOptions(options: ContactsDataCollectorOptions?)

    /**
     * Requests access to the device's contact data.
     *
     * @return `true` if access to contact data is granted; `false` otherwise.
     *
     * @throws Throwable if there is an error during the access request process.
     */
    @Throws(Throwable::class)
    suspend fun requestAccess(): Boolean

    /**
     * Loads a list of contacts from the device's storage.
     *
     * @return A list of [Contact] objects representing the loaded contacts.
     *
     * @throws Throwable if there is an error during the contact data loading process.
     */
    @Throws(Throwable::class)
    suspend fun loadContacts(): List<Contact>
}
