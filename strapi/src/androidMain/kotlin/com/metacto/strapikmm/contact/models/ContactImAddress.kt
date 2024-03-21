package com.metacto.strapikmm.contact.models

import kotlinx.serialization.Serializable

@Serializable
internal data class ContactImAddress(
    val type: String,
    val value: String
)