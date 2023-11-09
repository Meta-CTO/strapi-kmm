package com.swensonhe.strapikmm.contact.models

import kotlinx.serialization.Serializable

/**
 * A data class representing a contact's relation to another person.
 *
 * @param type The type or nature of the relation (e.g., "spouse," "sibling," "parent," etc.).
 * @param value The specific value describing the relation (e.g., the name or identifier of the related person).
 */
@Serializable
internal data class ContactRelation(
    val type: String,
    val value: String
)