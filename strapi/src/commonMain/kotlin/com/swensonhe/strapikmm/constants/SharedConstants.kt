package com.swensonhe.strapikmm.constants

/**
 * An object that holds shared constants used throughout the Library.
 * These constants are used for various purposes, such as key names, header values, and database names.
 */
object SharedConstants {
    /**
     * The key for an access token, used for authentication.
     */
    const val ACCESS_TOKEN = "access_token"

    /**
     * The key for cached user data.
     */
    const val CACHED_USER_DATA = "CACHED_USER_DATA"

    /**
     * The header key for an authorization token.
     */
    const val AUTHORIZATION_HEADER = "Authorization"

    /**
     * The key for an email used during email link sign-in.
     */
    const val SIGN_IN_EMAIL_LINK_EMAIL = "SIGN_IN_EMAIL_LINK_EMAIL"

    /**
     * The value for the "Bearer" token prefix often used in authorization headers.
     */
    const val BEARER = "Bearer"

    /**
     * The name of the application's database.
     */
    const val APP_DATABASE_NAME = "AppDatabase"

    /**
     * The key for the cached app configuration date.
     */
    const val CACHED_APP_CONFIG_DATE = "CACHED_APP_CONFIG_DATE"

    /**
     * The key for the cached app configuration version.
     */
    const val CACHED_APP_CONFIG_VERSION = "CACHED_APP_CONFIG_VERSION"

    /**
     * The key for the cached app configuration data.
     */
    const val CACHED_APP_CONFIG = "CACHED_APP_CONFIG"

    /**
     * The key for a verification phone number (used for phone number authentication).
     */
    const val VERIFICATION_PHONE_NUMBER = "VERIFICATION_PHONE_NUMBER"

    /**
     * The key for a verification phone number verification ID (used for phone number authentication).
     */
    const val VERIFICATION_PHONE_NUMBER_VERIFICATION_ID = "VERIFICATION_PHONE_NUMBER_VERIFICATION_ID"
}
