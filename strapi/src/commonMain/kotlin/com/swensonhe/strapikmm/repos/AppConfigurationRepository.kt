package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.StrapiQueryBuilder
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.DataWrapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.DatetimeUtil
import com.swensonhe.strapikmm.util.isBefore1DayFromNow
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Repository for managing the application configuration data.
 *
 * @param appConfigurationService The service responsible for fetching the configuration data.
 * @param sharedPreference Shared preferences to store and retrieve cached configuration data.
 */
class AppConfigurationRepository(
    val appConfigurationService: StrapiService,
    val sharedPreference: KmmPreference
) {

    /**
     * Retrieve the application configuration data, caching it if necessary.
     *
     * @param T The expected type of the application configuration data.
     * @param appConfigurationQueryBuilder Custom query builder for configuration data.
     * @param currentAppConfigurationVersion The current version of the configuration data.
     * @return The deserialized application configuration data of type [T].
     * @throws Throwable in case of exceptions during the retrieval or parsing of data.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> getAppConfiguration(
        noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        currentAppConfigurationVersion: Int
    ): T {
        // Retrieve cached configuration data if available.
        val cachedAppConfiguration = sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG)
        // Retrieve cached configuration date if available.
        val cachedAppConfigurationDate =
            sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG_DATE)
        // Retrieve cached configuration version if available.
        val cachedAppConfigurationVersion =
            sharedPreference.getInt(SharedConstants.CACHED_APP_CONFIG_VERSION, 0)

        val loadAppConfiguration: suspend (() -> T) = {
            // Fetch the configuration data from the server.
            val newAppConfiguration = loadAppConfiguration<T>(appConfigurationQueryBuilder)
            // Convert the configuration data to a string.
            val appConfigurationAsString = Json.encodeToString(newAppConfiguration)
            // Cache the configuration data.
            sharedPreference.putString(SharedConstants.CACHED_APP_CONFIG, appConfigurationAsString)
            // Cache the configuration date.
            sharedPreference.putInt(
                SharedConstants.CACHED_APP_CONFIG_VERSION,
                currentAppConfigurationVersion
            )
            // Cache the configuration version.
            sharedPreference.putString(
                SharedConstants.CACHED_APP_CONFIG_DATE,
                DatetimeUtil.now().toString()
            )

            // Return the configuration data.
            newAppConfiguration
        }
        // Check if the cached configuration data is still valid, based on the date and version. If so, return it.
        // Otherwise, fetch the configuration data from the server.
        if (cachedAppConfiguration.isNullOrEmpty().not() && // Check if the cached configuration data is not null or empty.
            cachedAppConfigurationDate.isNullOrEmpty().not() && // Check if the cached configuration date is not null or empty.
            LocalDateTime.parse(cachedAppConfigurationDate!!).isBefore1DayFromNow().not() && // Check if the cached configuration date is not older than 1 day.
            cachedAppConfigurationVersion == currentAppConfigurationVersion // Check if the cached configuration version is the same as the current configuration version.
        ) {
            // Return the cached configuration data.
            return try {
                // Deserialize the cached configuration data.
                Json.decodeFromString(cachedAppConfiguration!!)
            } catch (exception: Exception) {
                // If the cached configuration data could not be deserialized, fetch the configuration data from the server.
                loadAppConfiguration()
            }
        }

        // Fetch the configuration data from the server.
        return loadAppConfiguration()
    }

    /**
     * Load and retrieve application configuration data from the service.
     *
     * @param appConfigurationQueryBuilder Custom query builder for configuration data.
     * @return The application configuration data of type [T] fetched from the service.
     * @throws Throwable in case of exceptions during the retrieval or parsing of data.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> loadAppConfiguration(noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit) =
        appConfigurationService.get<DataWrapper<T>> {
            endpoint("/app-configuration")
            strapiQueryBuilder(appConfigurationQueryBuilder)
        }.data

    /**
     * Get the cached application configuration data of type [T].
     *
     * @return The cached application configuration data, or null if not available.
     */
    inline fun <reified T> getCachedAppConfiguration(): T? {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(cachedData)
        }
    }
}
