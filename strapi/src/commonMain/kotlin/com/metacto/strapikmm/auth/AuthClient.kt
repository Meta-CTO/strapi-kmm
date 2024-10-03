package com.metacto.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential

expect class AuthClient() : AuthProvider {
    fun init()
    fun setAuthOptions(options: AuthOptions)
    fun signOut()
    override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    )

    override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    )
}

expect class AuthOptions