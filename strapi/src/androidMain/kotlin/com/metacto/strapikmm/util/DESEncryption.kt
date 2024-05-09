@file:OptIn(ExperimentalEncodingApi::class)

package com.metacto.strapikmm.util

import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object CryptoConstants {
    const val ALGORITHM_DES = "DES/CBC/PKCS5Padding"
}

actual class DESEncryption actual constructor(private val key: String, private val iv: String) {
    actual fun encrypt(input: String): String {
        return Base64.Default.encode((encrypt(input, key, iv) ?: byteArrayOf()))
    }

    actual fun decrypt(input: String): String {
        val byteArray = Base64.Default.decode(input)
        return decrypt(byteArray, key, iv) ?: ""
    }

    private fun encryptToHex(originStr: String, secretKey: String, iv: String): String {
        return byte2hex(encrypt(originStr, secretKey, iv))
    }

    private fun decryptFromHex(encryptHexStr: String?, secretKey: String, iv: String?): String? {
        if (encryptHexStr == null || iv == null) return null
        try {
            return decrypt(hex2byte(encryptHexStr.toByteArray()), secretKey, iv)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private fun encrypt(data: String?, key: String, iv: String?): ByteArray? {
        if (data == null || iv == null) return null
        try {
            val dks = DESKeySpec(key.toByteArray())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            //key的长度不能够小于8位字节
            val secretKey: Key = keyFactory.generateSecret(dks)
            val cipher = Cipher.getInstance(CryptoConstants.ALGORITHM_DES)
            val paramSpec: AlgorithmParameterSpec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec)
            return cipher.doFinal(data.toByteArray())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
    }
    private fun decrypt(data: ByteArray?, key: String, iv: String?): String? {
        if (data == null || iv == null) return null
        try {
            val dks = DESKeySpec(key.toByteArray())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            //key的长度不能够小于8位字节
            val secretKey: Key = keyFactory.generateSecret(dks)
            val cipher = Cipher.getInstance(CryptoConstants.ALGORITHM_DES)
            val paramSpec: AlgorithmParameterSpec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec)
            return String(cipher.doFinal(data))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return ""
        }
    }
    private fun byte2hex(b: ByteArray?): String {
        val hs = StringBuilder()
        var stmp: String
        var n = 0
        while (b != null && n < b.size) {
            stmp = Integer.toHexString(b[n].toInt() and 0XFF)
            if (stmp.length == 1) hs.append('0')
            hs.append(stmp)
            n++
        }
        return hs.toString().uppercase(Locale.getDefault())
    }

    private fun hex2byte(b: ByteArray): ByteArray {
        require((b.size % 2) == 0)
        val b2 = ByteArray(b.size / 2)
        var n = 0
        while (n < b.size) {
            val item = String(b, n, 2)
            b2[n / 2] = item.toInt(16).toByte()
            n += 2
        }
        return b2
    }
}