package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseAuth.FIROAuthProvider
import dev.gitlive.firebase.auth.AuthCredential

actual class AuthClient actual constructor(
    activity: Any?,
    private val authStateChangeListener: OnAuthStateChangeListener
) :
    AuthProvider {

    actual fun init() {}

    private val signInWithAppleProvider = SignInWithAppleProvider(
        onSuccess = {
            val credential = FIROAuthProvider.credentialWithProviderID(
                providerID = "apple.com",
                IDToken = it,
                rawNonce = "",
                accessToken = null
            )

            authStateChangeListener.onAuthStateChanged(AuthCredential(credential))
        },
        onFailure = {
            println(it)
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

            authStateChangeListener.onAuthStateChanged(AuthCredential(credential))
        },
        onFailure = {
            println(it)
        }
    )

    override fun signInWithGoogle() {
        signInWithGoogleProvider.start()
    }

    override fun signInWithApple() {
        signInWithAppleProvider.start()
    }
}

