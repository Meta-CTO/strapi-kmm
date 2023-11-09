
package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Represents a request to update the time zone settings.
 *
 * @property timeZone The time zone to be set, e.g., "Africa/Cairo", "Europe/London", "America/New_York" etc.
 */

@Serializable
@JsExport
data class UpdateTimeZoneRequest(
    @SerialName("timeZone")
    val timeZone: String?
)
