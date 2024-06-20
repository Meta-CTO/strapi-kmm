package com.metacto.strapikmm.repos

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.StrapiQueryBuilder
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.datasource.network.services.strapi.StrapiService
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.model.UpdateTimeZoneRequest
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.util.asCommonFlow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
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
    val sharedPreference: KmmPreference,
    private val logoutUseCase: LogoutUseCase
) {
    val userBroadcastChannel = BroadcastChannel<Unit>(Channel.BUFFERED)

    @OptIn(ObsoleteCoroutinesApi::class)
    fun subscribeToUserBroadcastChannel() =
        userBroadcastChannel.openSubscription().consumeAsFlow().asCommonFlow()

    @Throws(Throwable::class)
    suspend inline fun <reified T> getCurrentUser(
        forceUpdate: Boolean = false,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T = executeCatching {
        val cachedUser = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        return if (cachedUser.isNullOrEmpty() || forceUpdate || cachedUser.trim() == "{}") {
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
    suspend fun deleteUserAccount(): Unit = executeCatching {
        val result = userService.delete<Unit> {
            endpoint("/users/me")
        }

        logoutUseCase.logout()

        return result
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> updateTimZone(
        timezone: String,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T = executeCatching {
        return updateUserData(UpdateTimeZoneRequest(timezone), userQueryBuilder)
    }

    @OptIn(ObsoleteCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend inline fun <reified T, reified D> updateUserData(
        data: D,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T = executeCatching {
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

    inline fun <reified T> saveUserData(user: T) = executeCatching {
        val userString = JsonWithIgnoredUnknownKeys.encodeToString(user)
        sharedPreference.putString(SharedConstants.CACHED_USER_DATA, userString)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    inline fun <reified T> getCachedUser(): T? = executeCatching {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            JsonWithIgnoredUnknownKeys.decodeFromString(cachedData)
        }
    }
}