package com.swensonhe.strapikmm.deeplink.model

import com.swensonhe.andrew_kmm.utilities.CommonParcelable
import com.swensonhe.andrew_kmm.utilities.CommonParcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@CommonParcelize
@Serializable
data class DeepLinkResult(
    @SerialName("deepLinkValue")
    val deepLinkValue: String?,
    @SerialName("campaign")
    val campaign: String?,
    @SerialName("campaignId")
    val campaignId: String?,
    @SerialName("clickHttpReferrer")
    val clickHttpReferrer: String?,
    @SerialName("isDeferred")
    val isDeferred: Boolean?,
    @SerialName("mediaSource")
    val mediaSource: String?,
    @SerialName("matchType")
    val matchType: String?,
    @SerialName("clickEventJson")
    val clickEventJson: String?
): CommonParcelable
