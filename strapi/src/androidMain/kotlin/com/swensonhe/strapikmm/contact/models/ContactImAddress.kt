package com.swensonhe.strapikmm.contact.models

import kotlinx.serialization.Serializable

/**
 * A data class representing an instant messaging (IM) address associated with a contact.
 *
 * @param type The type of the IM address (e.g., "AIM," "Skype," "Google Hangouts").
 * @param value The actual IM address value (e.g., the IM username or ID).
 */
@Serializable
internal data class ContactImAddress(
    val type: String,
    val value: String
)