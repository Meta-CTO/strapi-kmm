package com.metacto.strapikmm.repos

import com.metacto.strapikmm.appconfigversion.AppConfigurationVersion
import com.metacto.strapikmm.appconfigversion.AppVersionValidator
import com.metacto.strapikmm.appconfigversion.UpdateType
import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.StrapiQueryBuilder
import com.metacto.strapikmm.datasource.network.services.strapi.StrapiService
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.model.DataWrapper
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.util.DatetimeUtil
import com.metacto.strapikmm.util.minutesFromNow
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppConfigurationRepository(
    val applicationContext: Any? = null,
    val appConfigurationService: StrapiService,
    val sharedPreference: KmmPreference,
    val appConfigurationExpirationInMinutes: Long
) {

    @Throws(Throwable::class)
    suspend inline fun <reified T : AppConfigurationVersion> getAppConfiguration(
        noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        currentAppConfigurationVersion: Int
    ): T = executeCatching {
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

        if (cachedAppConfiguration != null && cachedAppConfigurationDate != null) {
            val minutesSinceCacheDate =
                LocalDateTime.parse(cachedAppConfigurationDate).minutesFromNow()

            if (
                cachedAppConfiguration.isNotEmpty() &&
                cachedAppConfigurationDate.isNotEmpty() &&
                minutesSinceCacheDate < appConfigurationExpirationInMinutes &&
                cachedAppConfigurationVersion == currentAppConfigurationVersion
            ) {
                return try {
                    Json.decodeFromString(cachedAppConfiguration)
                } catch (exception: Exception) {
                    loadAppConfiguration()
                }
            }
        }

        return loadAppConfiguration()
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T : AppConfigurationVersion> loadAppConfiguration(noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit) =
        appConfigurationService.get<DataWrapper<T>> {
            endpoint("/app-configuration")
            strapiQueryBuilder(appConfigurationQueryBuilder)
        }.data

    inline fun <reified T> getCachedAppConfiguration(): T? = executeCatching {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_APP_CONFIG)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(cachedData)
        }
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T : AppConfigurationVersion> checkAppUpdates(
        noinline appConfigurationQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        currentAppConfigurationVersion: Int
    ): UpdateType {
        val appConfiguration = getAppConfiguration<T>(
            appConfigurationQueryBuilder,
            currentAppConfigurationVersion
        )

        return AppVersionValidator.checkRequiredUpdate(
            appConfiguration.applicationVersions,
            applicationContext
        )
    }
}

