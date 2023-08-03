package com.swensonhe.strapikmm.auth

import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    fun signInWithGoogle()
    fun signInWithApple()
}

interface OnAuthStateChangeListener {
    fun onAuthStateChanged(credential: AuthCredential)
}