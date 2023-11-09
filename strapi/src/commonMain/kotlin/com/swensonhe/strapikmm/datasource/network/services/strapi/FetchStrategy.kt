package com.swensonhe.strapikmm.datasource.network.services.strapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enumeration representing different data fetching strategies.
 *
 * Use this enum to specify how data should be fetched, either from a remote data source, a local cache,
 * or a combination of both.
 */
@Serializable
enum class FetchStrategy {
    /**
     * Data should be fetched from the remote data source.
     */
    @SerialName("remote")
    REMOTE,

    /**
     * Data should be first attempted to be fetched from the local cache if available, then
     * fetch it from the remote data source.
     */
    @SerialName("cacheThenRemote")
    CACHE_THEN_REMOTE
}
