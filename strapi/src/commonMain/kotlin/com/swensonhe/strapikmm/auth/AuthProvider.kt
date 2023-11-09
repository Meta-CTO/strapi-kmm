package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential

/**
 * An interface for an authentication provider, which defines methods for signing in with various authentication methods.
 */
interface AuthProvider {
    /**
     * Sign in using Google authentication.
     *
     * @param onSuccess A callback to be invoked when the authentication is successful.
     *                 It receives an [AuthCredential] object representing the user's credentials.
     * @param onFail A callback to be invoked when the authentication fails.
     *              It receives a [Throwable] containing information about the failure.
     */
    fun signInWithGoogle(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit)

    /**
     * Sign in using Apple authentication.
     *
     * @param onSuccess A callback to be invoked when the authentication is successful.
     *                 It receives an [AuthCredential] object representing the user's credentials.
     * @param onFail A callback to be invoked when the authentication fails.
     *              It receives a [Throwable] containing information about the failure.
     */
    fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit)
}
