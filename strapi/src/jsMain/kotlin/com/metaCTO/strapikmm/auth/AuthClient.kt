package com.metaCTO.strapikmm.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firebase

actual class AuthClient : AuthProvider {

    actual fun init() {}

    override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit
    ) {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.OAuthProvider("apple.com")
        ).then {
            val profile = ProfileMetadata(
                firstName = it.user?.displayName,
                lastName = null,
                email = it.user?.email,
                phoneNumber = it.user?.phoneNumber,
                pictureUrl = it.user?.photoURL
            )
            onSuccess.invoke(AuthCredential(it.credential!!), profile)
        }.catch {
            onFail.invoke(it)
        }
    }

    override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit
    ) {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.GoogleAuthProvider()
        ).then {
            val profile = ProfileMetadata(
                firstName = it.user?.displayName,
                lastName = null,
                email = it.user?.email,
                phoneNumber = it.user?.phoneNumber,
                pictureUrl = it.user?.photoURL
            )
            onSuccess.invoke(AuthCredential(it.credential!!), profile)
        }.catch {
            onFail.invoke(it)
        }
    }

    actual fun setAuthOptions(options: AuthOptions?) {
    }
}

actual class AuthOptions