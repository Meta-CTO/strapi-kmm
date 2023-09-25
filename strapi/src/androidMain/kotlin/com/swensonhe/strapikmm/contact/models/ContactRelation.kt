package com.swensonhe.strapikmm.contact.models

import kotlinx.serialization.Serializable

@Serializable
internal data class ContactRelation(
    val type: String,
    val value: String
)