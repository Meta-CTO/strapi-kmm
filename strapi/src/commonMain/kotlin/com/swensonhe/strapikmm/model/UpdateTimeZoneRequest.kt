
package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class UpdateTimeZoneRequest(
    @SerialName("timeZone")
    val timeZone: String?
)
