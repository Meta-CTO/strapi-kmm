package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.StrapiQueryBuilder
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.DataWrapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.DatetimeUtil
import com.swensonhe.strapikmm.util.isBefore1DayFromNow
import com.swensonhe.strapikmm.util.minutesFromNow
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppConfigurationRepository(
    val appConfigurationService: StrapiService,
    val sharedPreference: KmmPreference,
    val appConfigurationExpirationInMinutes: Long,
) {

    @Throws(Throwable::class)
    suspend inline fun <reified T> getAppConfiguration(
        noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        currentAppConfigurationVersion: Int
    ): T {
        val cachedAppConfiguration = sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG)
        val cachedAppConfigurationDate =
            sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG_DATE)
        val cachedAppConfigurationVersion =
            sharedPreference.getInt(SharedConstants.CACHED_APP_CONFIG_VERSION, 0)

        val loadAppConfiguration: suspend (() -> T) = {
            val newAppConfiguration = loadAppConfiguration<T>(appConfigurationQueryBuilder)
            val appConfigurationAsString = Json.encodeToString(newAppConfiguration)
            sharedPreference.putString(SharedConstants.CACHED_APP_CONFIG, appConfigurationAsString)
            sharedPreference.putInt(
                SharedConstants.CACHED_APP_CONFIG_VERSION,
                currentAppConfigurationVersion
            )
            sharedPreference.putString(
                SharedConstants.CACHED_APP_CONFIG_DATE,
                DatetimeUtil.now().toString()
            )

            newAppConfiguration
        }

        if (cachedAppConfiguration.isNullOrEmpty().not() &&
            cachedAppConfigurationDate.isNullOrEmpty().not() &&
            LocalDateTime.parse(cachedAppConfigurationDate!!).minutesFromNow() >= appConfigurationExpirationInMinutes &&
            cachedAppConfigurationVersion == currentAppConfigurationVersion
        ) {
            return try {
                Json.decodeFromString(cachedAppConfiguration!!)
            } catch (exception: Exception) {
                loadAppConfiguration()
            }
        }

        return loadAppConfiguration()
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> loadAppConfiguration(noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit) =
        appConfigurationService.get<DataWrapper<T>> {
            endpoint("/app-configuration")
            strapiQueryBuilder(appConfigurationQueryBuilder)
        }.data

    inline fun <reified T> getCachedAppConfiguration(): T? {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(cachedData)
        }
    }
}
