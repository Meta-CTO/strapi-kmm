@file:OptIn(ExperimentalForeignApi::class)

package com.metacto.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

actual class AuthOptions(
    var presentingViewController: UIViewController?
)

actual class AuthClient : AuthProvider {
    private lateinit var onResult: (AuthCredential, ProfileMetadata) -> Unit
    private lateinit var onError: (Throwable) -> Unit
    private lateinit var options: AuthOptions

    private var signInWithAppleProvider: SignInWithAppleProvider? = null
    private var signInWithGoogleProvider: SignInWithGoogleProvider? = null
    actual fun init() {}

    private fun createSignInWithAppleProvider() {
        val provider =  SignInWithAppleProvider(
            onSuccess = { token, profileMetadata ->
                val credential = FIROAuthProvider.credentialWithProviderID(
                    providerID = "apple.com",
                    IDToken = token,
                    rawNonce = "",
                    accessToken = null
                )

                onResult.invoke(AuthCredential(credential), profileMetadata)
                signInWithAppleProvider = null
            },
            onFailure = {
                onError.invoke(it)
                signInWithAppleProvider = null
            }
        )

        signInWithAppleProvider = provider
    }

    private fun createSignInWithGoogleProvider() {
        if (options.presentingViewController == null) throw Throwable("PresentingViewController cannot be null")
        val provider =  SignInWithGoogleProvider(
            presentingViewController = options.presentingViewController!!,
            onSuccess = { token, profileMetadata ->
                val credential = FIROAuthProvider.credentialWithProviderID(
                    providerID = "google.com",
                    IDToken = token,
                    rawNonce = "",
                    accessToken = null
                )

                onResult.invoke(AuthCredential(credential), profileMetadata)
                signInWithGoogleProvider = null
                options.presentingViewController = null
            },
            onFailure = {
                onError.invoke(it)
                signInWithGoogleProvider = null
                options.presentingViewController = null
            }
        )

        signInWithGoogleProvider = provider
    }
//
//    private val signInWithAppleProvider by lazy {
//        SignInWithAppleProvider(
//            onSuccess = { token, profileMetadata ->
//                val credential = FIROAuthProvider.credentialWithProviderID(
//                    providerID = "apple.com",
//                    IDToken = token,
//                    rawNonce = "",
//                    accessToken = null
//                )
//
//                onResult.invoke(AuthCredential(credential), profileMetadata)
//            },
//            onFailure = {
//                onError.invoke(it)
//            }
//        )
//    }
//
//    private val signInWithGoogleProvider by lazy {
//        SignInWithGoogleProvider(
//            presentingViewController = options.presentingViewController,
//            onSuccess = { token, profileMetadata ->
//                val credential = FIROAuthProvider.credentialWithProviderID(
//                    providerID = "google.com",
//                    IDToken = token,
//                    rawNonce = "",
//                    accessToken = null
//                )
//
//                onResult.invoke(AuthCredential(credential), profileMetadata)
//            },
//            onFailure = {
//                onError.invoke(it)
//            }
//        )
//    }

    override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        createSignInWithGoogleProvider()
        this.onResult = onSuccess
        this.onError = onFail
        signInWithGoogleProvider?.start()
    }

    override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        createSignInWithAppleProvider()
        this.onResult = onSuccess
        this.onError = onFail
        signInWithAppleProvider?.start()
    }

    actual fun setAuthOptions(options: AuthOptions) {
        this.options = options
    }
}

