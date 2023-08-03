package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firebase

actual class AuthClient actual constructor(activity: Any?, private val authStateChangeListener: OnAuthStateChangeListener) : AuthProvider {

    actual fun init() {}

    override fun signInWithApple() {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.OAuthProvider("apple.com")
        ).then {
            authStateChangeListener.onAuthStateChanged(
                AuthCredential(it.credential!!)
            )
        }.catch {
            println("Error sign in in with google: $it")
        }
    }

    override fun signInWithGoogle() {
        Firebase.auth.js.signInWithPopup(
            firebase.auth.GoogleAuthProvider()
        ).then {
            authStateChangeListener.onAuthStateChanged(
                AuthCredential(it.credential!!)
            )
        }.catch {
            println("Error sign in in with google: $it")
        }
    }
}