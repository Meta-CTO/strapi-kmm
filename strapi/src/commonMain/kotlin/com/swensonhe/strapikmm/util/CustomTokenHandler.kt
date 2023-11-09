package com.swensonhe.strapikmm.util

object CustomTokenHandler {
    private var token = ""

    fun getToken(): String {
        return token
    }
    
    fun setToken(newToken: String) {
        token = newToken
    }
    
    fun clearToken() {
        token = ""
    }
    
    fun hasToken(): Boolean {
        return token.isNotEmpty()
    }
}