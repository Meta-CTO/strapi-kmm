package com.metaCTO.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import platform.UIKit.UIViewController

actual class AuthOptions(
    val presentingViewController: UIViewController
)

actual class AuthClient : AuthProvider {
    private lateinit var onResult: (AuthCredential, ProfileMetadata) -> Unit
    private lateinit var onError: (Throwable) -> Unit
    private lateinit var options: AuthOptions

    actual fun init() {}

    private val signInWithAppleProvider by lazy {
        SignInWithAppleProvider(
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
    }

    private val signInWithGoogleProvider by lazy {
        SignInWithGoogleProvider(
            presentingViewController = options.presentingViewController,
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
    }

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
        if (options == null) throw IllegalArgumentException("options cannot be null")
        this.options = options
    }
}

