package com.swensonhe.strapikmm.deeplink.model

import com.swensonhe.andrew_kmm.utilities.CommonParcelable
import com.swensonhe.andrew_kmm.utilities.CommonParcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
class BaseUrl(
    @SerialName("oneLinkID")
    val oneLinkID: String,
    @SerialName("domain")
    val domain: String,
    @SerialName("appPackage")
    val appPackage: String
): CommonParcelable