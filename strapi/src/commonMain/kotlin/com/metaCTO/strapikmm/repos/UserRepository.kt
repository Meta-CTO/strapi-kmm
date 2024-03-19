package com.metaCTO.strapikmm.repos

import com.metaCTO.strapikmm.constants.SharedConstants
import com.metaCTO.strapikmm.datasource.network.StrapiQueryBuilder
import com.metaCTO.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metaCTO.strapikmm.datasource.network.services.strapi.StrapiService
import com.metaCTO.strapikmm.model.UpdateTimeZoneRequest
import com.metaCTO.strapikmm.sharedpreference.KmmPreference
import com.metaCTO.strapikmm.util.asCommonFlow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class UserRepository(
    val userService: StrapiService,
    val sharedPreference: KmmPreference
) {
    val userBroadcastChannel = BroadcastChannel<Unit>(Channel.BUFFERED)

    @OptIn(ObsoleteCoroutinesApi::class)
    fun subscribeToUserBroadcastChannel() =
        userBroadcastChannel.openSubscription().consumeAsFlow().asCommonFlow()

    @Throws(Throwable::class)
    suspend inline fun <reified T> getCurrentUser(
        forceUpdate: Boolean = false,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T {
        val cachedUser = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        return if (cachedUser.isNullOrEmpty() || forceUpdate) {
            val user = userService.get<T> {
                endpoint("/users/me")
                strapiQueryBuilder(userQueryBuilder)
            }
            saveUserData(user)
            user
        } else {
            JsonWithIgnoredUnknownKeys.decodeFromString(cachedUser)
        }
    }

    fun refreshUserProfileSilently() {
        GlobalScope.launch {
            try {
                getCurrentUser(true)
            } catch (_: Throwable) {
            }
        }
    }

    @Throws(Throwable::class)
    suspend fun deleteUserAccount() = userService.delete<Unit> {
        endpoint("/users/me")
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> updateTimZone(timezone: String): T =
        updateUserData(UpdateTimeZoneRequest(timezone))


    @OptIn(ObsoleteCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend inline fun <reified T, reified D> updateUserData(
        data: D,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T {
        val updatedUser = userService.put<T> {
            endpoint("/users/me")
            strapiQueryBuilder(userQueryBuilder)
            body(data)
        }

        val userString = JsonWithIgnoredUnknownKeys.encodeToString(updatedUser)
        sharedPreference.putString(SharedConstants.CACHED_USER_DATA, userString)

        // Broadcast user changes
        GlobalScope.launch { userBroadcastChannel.send(Unit) }

        return updatedUser
    }

    inline fun <reified T> saveUserData(user: T) {
        val userString = JsonWithIgnoredUnknownKeys.encodeToString(user)
        sharedPreference.putString(SharedConstants.CACHED_USER_DATA, userString)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    inline fun <reified T> getCachedUser(): T? {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            JsonWithIgnoredUnknownKeys.decodeFromString(cachedData)
        }
    }
}