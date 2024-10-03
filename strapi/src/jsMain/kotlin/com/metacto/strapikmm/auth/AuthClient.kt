package com.metacto.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.externals.GoogleAuthProvider
import dev.gitlive.firebase.auth.externals.OAuthProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual class AuthClient : AuthProvider {

    actual fun init() {}

    actual override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit
    ) {
        GlobalScope.launch {
            try {
                val result = Firebase.auth.signInWithPopup(
                    OAuthProvider("apple.com")
                )
                val profile = ProfileMetadata(
                    firstName = result.user?.displayName,
                    lastName = null,
                    email = result.user?.email,
                    phoneNumber = result.user?.phoneNumber,
                    pictureUrl = result.user?.photoURL
                )

                onSuccess.invoke(AuthCredential(result.js.credential!!), profile)
            } catch (e: Throwable) {
                onFail.invoke(e)
            }
        }
    }

    actual override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit
    ) {
        GlobalScope.launch {
            try {
                val result = Firebase.auth.signInWithPopup(
                    GoogleAuthProvider()
                )
                val profile = ProfileMetadata(
                    firstName = result.user?.displayName,
                    lastName = null,
                    email = result.user?.email,
                    phoneNumber = result.user?.phoneNumber,
                    pictureUrl = result.user?.photoURL
                )
                onSuccess.invoke(AuthCredential(result.js.credential!!), profile)
            } catch (e: Throwable) {
                onFail.invoke(e)
            }
        }
    }

    actual fun setAuthOptions(options: AuthOptions) {
    }

    actual fun signOut() {
    }
}

actual class AuthOptions
