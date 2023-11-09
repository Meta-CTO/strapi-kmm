package com.swensonhe.strapikmm.contact.models

import kotlinx.serialization.Serializable

/**
 * A data class representing a postal address associated with a contact.
 *
 * @param street The street address.
 * @param poBox The post office box (P.O. Box) number if applicable.
 * @param neighborhood The neighborhood or locality.
 * @param city The city or locality.
 * @param region The region or state.
 * @param postCode The postal code or ZIP code.
 * @param country The country name.
 */
@Serializable
internal data class ContactPostalAddress(
    val street: String,
    val poBox: String,
    val neighborhood: String,
    val city: String,
    val region: String,
    val postCode: String,
    val country: String
)
