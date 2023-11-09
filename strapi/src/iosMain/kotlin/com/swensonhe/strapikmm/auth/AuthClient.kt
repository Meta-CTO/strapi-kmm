package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential

/**
 * The [AuthOptions] class represents the authentication options for the authentication process.
 * This class provides configuration options for the [AuthClient].
 */
actual class AuthOptions

/**
 * The [AuthClient] class is responsible for authenticating users using various providers
 * such as Google Sign-In and Apple Sign-In.
 */
actual class AuthClient: AuthProvider {
    private lateinit var onResult: (AuthCredential) -> Unit
    private lateinit var onError: (Throwable) -> Unit

    /**
     * Initializes the authentication client. You must call this method before performing any
     * authentication operations.
     */
    actual fun init() {}

    /**
     * Signs in the user using Apple Sign-In.
     *
     * @param onSuccess The callback to invoke when the user is successfully signed in.
     * @param onFail The callback to invoke when the user sign in fails.
     */
    private val signInWithAppleProvider = SignInWithAppleProvider(
        onSuccess = {

            // Create an `FIROAuthCredential` from the Apple ID credential.
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "apple.com",
                IDToken = it,
                rawNonce = "",
                accessToken = null
            )

            // Send the credential to the Firebase app.
            onResult.invoke(AuthCredential(credential))
        },
        onFailure = {
            // Invoke the failure callback.
            onError.invoke(it)
        }
    )

    /**
     * Signs in the user using Google Sign-In.
     *
     * @param onSuccess The callback to invoke when the user is successfully signed in.
     * @param onFail The callback to invoke when the user sign in fails.
     */
    private val signInWithGoogleProvider = SignInWithGoogleProvider(
        onSuccess = {
            // Create an `FIROAuthCredential` from the Google ID token.
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "google.com",
                IDToken = it,
                rawNonce = "",
                accessToken = null
            )

            // Send the credential to the Firebase app.
            onResult.invoke(AuthCredential(credential))
        },
        onFailure = {
            // Invoke the failure callback.
            onError.invoke(it)
        }
    )

    /**
     * Signs in the user using Google Sign-In.
     *
     * @param onSuccess The callback to invoke when the user is successfully signed in.
     * @param onFail The callback to invoke when the user sign in fails.
     */
    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        // Store the callbacks.
        this.onResult = onSuccess
        this.onError = onFail
        // Start the Google Sign-In process.
        signInWithGoogleProvider.start()
    }

    /**
     * Signs in the user using Apple Sign-In.
     *
     * @param onSuccess The callback to invoke when the user is successfully signed in.
     * @param onFail The callback to invoke when the user sign in fails.
     */
    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        this.onResult = onSuccess
        this.onError = onFail
        // Start the Apple Sign-In process.
        signInWithAppleProvider.start()
    }

    // Set the authentication options (not used on iOS).
    actual fun setAuthOptions(options: AuthOptions?) {
    }
}

