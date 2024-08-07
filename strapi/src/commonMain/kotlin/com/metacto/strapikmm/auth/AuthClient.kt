package com.metacto.strapikmm.auth

expect class AuthClient() : AuthProvider {
    fun init()
    fun setAuthOptions(options: AuthOptions)
    fun signOut()
}

expect class AuthOptions