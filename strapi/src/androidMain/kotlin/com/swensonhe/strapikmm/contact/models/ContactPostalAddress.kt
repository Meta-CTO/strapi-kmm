package com.swensonhe.strapikmm.contact.models

import kotlinx.serialization.Serializable

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
