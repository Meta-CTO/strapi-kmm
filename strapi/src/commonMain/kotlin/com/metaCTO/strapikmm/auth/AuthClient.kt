package com.metaCTO.strapikmm.auth

expect class AuthClient() : AuthProvider {
    fun init()
    fun setAuthOptions(options: AuthOptions?)
}

expect class AuthOptions