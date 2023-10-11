package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential

actual class AuthOptions

actual class AuthClient : AuthProvider {
    private lateinit var onResult: (AuthCredential, ProfileMetadata) -> Unit
    private lateinit var onError: (Throwable) -> Unit

    actual fun init() {}

    private val signInWithAppleProvider = SignInWithAppleProvider(
        onSuccess = { token, profileMetadata ->
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "apple.com",
                IDToken = token,
                rawNonce = "",
                accessToken = null
            )

            onResult.invoke(AuthCredential(credential), profileMetadata)
        },
        onFailure = {
            onError.invoke(it)
        }
    )

    private val signInWithGoogleProvider = SignInWithGoogleProvider(
        onSuccess = { token, profileMetadata ->
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "google.com",
                IDToken = token,
                rawNonce = "",
                accessToken = null
            )

            onResult.invoke(AuthCredential(credential), profileMetadata)
        },
        onFailure = {
            onError.invoke(it)
        }
    )

    override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.onResult = onSuccess
        this.onError = onFail
        signInWithGoogleProvider.start()
    }

    override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.onResult = onSuccess
        this.onError = onFail
        signInWithAppleProvider.start()
    }

    actual fun setAuthOptions(options: AuthOptions?) {
    }
}

