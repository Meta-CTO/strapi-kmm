package com.metacto.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    fun signInWithGoogle(onSuccess:(AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit)
    fun signInWithApple(onSuccess:(AuthCredential, ProfileMetadata) -> Unit, onFail: (Throwable) -> Unit)
}