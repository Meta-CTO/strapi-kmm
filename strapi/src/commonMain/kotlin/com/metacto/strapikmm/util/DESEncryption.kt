package com.metacto.strapikmm.util

expect class DESEncryption(key: String, iv: String) {
    fun encrypt(input: String): String
    fun decrypt(input: String): String
}
