package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    fun signInWithGoogle(onSuccess:(AuthCredential) -> Unit, onFail: (Throwable) -> Unit)
    fun signInWithApple(onSuccess:(AuthCredential) -> Unit, onFail: (Throwable) -> Unit)
}