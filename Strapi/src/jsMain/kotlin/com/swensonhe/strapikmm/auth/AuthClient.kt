package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firebase
actual class AuthClient actual constructor(options: AuthOptions?) : AuthProvider {

    actual fun init() {}

    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.OAuthProvider("apple.com")
        ).then {
            onSuccess.invoke(AuthCredential(it.credential!!))
        }.catch {
            onFail.invoke(it)
        }
    }

    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.GoogleAuthProvider()
        ).then {
            onSuccess.invoke(AuthCredential(it.credential!!))
        }.catch {
            onFail.invoke(it)
        }
    }
}

actual class AuthOptions