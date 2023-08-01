package com.swensonhe.strapikmm.datasource.network.services.strapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FetchStrategy {
    @SerialName("remote")
    REMOTE,
    @SerialName("cacheThenRemote")
    CACHE_THEN_REMOTE
}