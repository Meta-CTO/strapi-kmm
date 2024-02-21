package com.swensonhe.strapikmm.deeplink.model

import com.swensonhe.andrew_kmm.utilities.CommonParcelable
import com.swensonhe.andrew_kmm.utilities.CommonParcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class AppAttributionResult(
    @SerialName("deepLinkResult")
    val isOrganic: Boolean,
    @SerialName("deepLinkResult")
    val extras: Map<Any, Any?>
): CommonParcelable