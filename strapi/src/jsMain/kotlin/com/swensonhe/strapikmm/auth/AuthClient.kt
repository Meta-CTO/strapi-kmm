package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firebase

/**
 * The [AuthClient] provides authentication services using Firebase Authentication for web platform
 * It implements the [AuthProvider] interface to provide Google Sign-In and Apple Sign-In.
 */
actual class AuthClient : AuthProvider {


    // Initialize Firebase Auth if needed
    actual fun init() {}

    /**
     * Sign in a user using Apple Sign-In on web platforms.
     *
     * @param onSuccess Callback invoked when the sign-in is successful, providing an [AuthCredential].
     * @param onFail Callback invoked when the sign-in fails, providing an error [Throwable].
     */
    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        Firebase.auth.js.signInWithPopup(
            // Set the provider to Apple.
            firebase.auth.OAuthProvider("apple.com")
        ).then {
            onSuccess.invoke(AuthCredential(it.credential!!))
        }.catch {
            onFail.invoke(it)
        }
    }

    /**
     * Sign in a user using Google Sign-In on web platform.
     *
     * @param onSuccess Callback invoked when the sign-in is successful, providing an [AuthCredential].
     * @param onFail Callback invoked when the sign-in fails, providing an error [Throwable].
     */
    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        Firebase.auth.js.signInWithPopup(
            // Set the provider to Google.
            firebase.auth.GoogleAuthProvider()
        ).then {
            onSuccess.invoke(AuthCredential(it.credential!!))
        }.catch {
            onFail.invoke(it)
        }
    }

    /**
     * Set authentication options. This is a no-op method for web platforms.
     *
     * @param options An [AuthOptions] object containing authentication options. Can be null.
     */
    actual fun setAuthOptions(options: AuthOptions?) {
        // no-op for web
    }
}

actual class AuthOptions