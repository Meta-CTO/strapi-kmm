package com.swensonhe.strapikmm.auth

/**
 * An expect class representing an authentication client that implements the [AuthProvider] interface.
 * Platform-specific implementations of this class should be provided for each platform (e.g., Web, Android, iOS).
 */
expect class AuthClient() : AuthProvider {

    /**
     * Initializes the authentication client.
     */
    fun init()

    /**
     * Sets authentication options for the client.
     *
     * @param options An instance of [AuthOptions] that contains authentication configuration options.
     */
    fun setAuthOptions(options: AuthOptions?)
}

/**
 * An expect class representing authentication options for an authentication client.
 * Platform-specific implementations of this class should be provided for each platform (e.g., Web, Android, iOS).
 */
expect class AuthOptions
