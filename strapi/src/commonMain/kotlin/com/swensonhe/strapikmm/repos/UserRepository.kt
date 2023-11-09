package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.StrapiQueryBuilder
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.UpdateTimeZoneRequest
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.asCommonFlow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
 * Repository for user-related operations.
 *
 * @property userService The service for handling user-related API calls.
 * @property sharedPreference The shared preferences service for storing user data.
 */
class UserRepository(
    val userService: StrapiService,
    val sharedPreference: KmmPreference
) {
    /**
     * Broadcast channel to notify subscribers of user-related changes.
     */
    val userBroadcastChannel = BroadcastChannel<Unit>(Channel.BUFFERED)

    /**
     * Subscribe to the user broadcast channel to receive user-related change notifications.
     *
     * @return A flow of user-related change notifications.
     */
    @OptIn(ObsoleteCoroutinesApi::class)
    fun subscribeToUserBroadcastChannel() =
        userBroadcastChannel.openSubscription().consumeAsFlow().asCommonFlow()

    /**
     * Get the current user data of the specified type [T].
     *
     * @param forceUpdate Set to true to force an update from the server.
     * @param userQueryBuilder Additional user query builder functions.
     * @return The user data of type [T].
     * @throws Throwable in case of exceptions during the operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> getCurrentUser(
        forceUpdate: Boolean = false,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T {
        // Retrieve cached user data if available.
        val cachedUser = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        // check if the user data is cached and not expired or if we want to force an update
        return if (cachedUser.isNullOrEmpty() || forceUpdate) {
            // Fetch the user data from the server.
            val user = userService.get<T> {
                endpoint("/users/me")
                // Add the custom query builder functions.
                strapiQueryBuilder(userQueryBuilder)
            }
            // Cache the user data.
            saveUserData(user)
            // Return the user data.
            user
        } else {
            // Return the cached user data.
            Json.decodeFromString(cachedUser)
        }
    }

    /**
     * Refresh the user profile silently by updating it in the background.
     */
    fun refreshUserProfileSilently() {
        GlobalScope.launch {
            try {
                // Update the user profile in the background.
                getCurrentUser(true)
            } catch (_: Throwable) {
            }
        }
    }

    /**
     * Delete the user account.
     *
     * @throws Throwable in case of exceptions during the operation.
     */
    @Throws(Throwable::class)
    suspend fun deleteUserAccount() = userService.delete<Unit> {
        endpoint("/users/me")
    }

    /**
     * Update the user's time zone.
     *
     * @param timezone The new time zone.
     * @return The updated user data.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> updateTimZone(timezone: String): T =
        updateUserData(UpdateTimeZoneRequest(timezone))


    /**
     * Update the user data.
     *
     * @param data The new user data.
     * @param userQueryBuilder Additional user query builder functions.
     * @return The updated user data.
     */
    @OptIn(ObsoleteCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend inline fun <reified T, reified D> updateUserData(
        data: D,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {}
    ): T {
        // Update the user data on the server.
        val updatedUser = userService.put<T> {
            // Set the endpoint to the current user.
            endpoint("/users/me")
            // Add the custom query builder functions.
            strapiQueryBuilder(userQueryBuilder)
            // Set the new user data.
            body(data)
        }

        // convert the updated user to a string
        val userString = Json.encodeToString(updatedUser)
        // Cache the user data.
        sharedPreference.putString(SharedConstants.CACHED_USER_DATA, userString)

        // Broadcast user changes to subscribers.
        GlobalScope.launch { userBroadcastChannel.send(Unit) }

        // Return the updated user data.
        return updatedUser
    }

    /**
     * Save the user data to the shared preferences.
     *
     * @param user The user data to be saved.
     */
    inline fun <reified T> saveUserData(user: T) {
        val userString = Json.encodeToString(user)
        sharedPreference.putString(SharedConstants.CACHED_USER_DATA, userString)
    }

    /**
     * Check if a user is currently logged in.
     *
     * @return true if the user is logged in; false otherwise.
     */
    fun isUserLoggedIn(): Boolean {
        // Check if the access token is available in the shared preferences.
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    inline fun <reified T> getCachedUser(): T? {
        val cachedData = sharedPreference.getString(SharedConstants.CACHED_USER_DATA)
        return if (cachedData.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(cachedData)
        }
    }
}