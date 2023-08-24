package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
actual class AuthOptions

actual class AuthClient: AuthProvider {
    private lateinit var onResult: (AuthCredential) -> Unit
    private lateinit var onError: (Throwable) -> Unit

    actual fun init() {}

    private val signInWithAppleProvider = SignInWithAppleProvider(
        onSuccess = {
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "apple.com",
                IDToken = it,
                rawNonce = "",
                accessToken = null
            )

            onResult.invoke(AuthCredential(credential))
        },
        onFailure = {
            onError.invoke(it)
        }
    )

    private val signInWithGoogleProvider = SignInWithGoogleProvider(
        onSuccess = {
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "google.com",
                IDToken = it,
                rawNonce = "",
                accessToken = null
            )

            onResult.invoke(AuthCredential(credential))
        },
        onFailure = {
            onError.invoke(it)
        }
    )

    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.onResult = onSuccess
        this.onError = onFail
        signInWithGoogleProvider.start()
    }

    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        this.onResult = onSuccess
        this.onError = onFail
        signInWithAppleProvider.start()
    }

    actual fun setAuthOptions(options: AuthOptions?) {
    }
}

