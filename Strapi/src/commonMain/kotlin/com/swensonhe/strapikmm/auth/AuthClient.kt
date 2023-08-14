package com.swensonhe.strapikmm.auth

expect class AuthClient(options: AuthOptions?) : AuthProvider {
    fun init()
}

expect class AuthOptions