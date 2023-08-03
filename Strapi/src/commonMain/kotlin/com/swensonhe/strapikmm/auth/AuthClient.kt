package com.swensonhe.strapikmm.auth

expect class AuthClient(
    activity: Any? = null,
    authStateChangeListener: OnAuthStateChangeListener
) : AuthProvider {
    fun init()
}