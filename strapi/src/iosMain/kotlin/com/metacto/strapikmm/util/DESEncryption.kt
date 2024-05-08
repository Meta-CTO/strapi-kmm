@file:OptIn(ExperimentalForeignApi::class)

package com.metacto.strapikmm.util
import com.metacto.strapikmm.common.encryption.DESEncryption
import kotlinx.cinterop.ExperimentalForeignApi

actual class DESEncryption actual constructor(key: String, iv: String) {
    private val desEncryption = DESEncryption(key = key, iv = iv)

    actual fun encrypt(input: String): String {
        return desEncryption.encryptString(input)
    }

    actual fun decrypt(input: String): String {
        return desEncryption.decryptString(input)
    }
}