package com.kommu.mylibrary

import com.metacto.strapikmm.appconfigversion.AppConfigurationVersion
import com.metacto.strapikmm.appconfigversion.AppVersion
import com.metacto.strapikmm.appconfigversion.UpdateType
import com.metacto.strapikmm.datasource.network.handleException
import com.metacto.strapikmm.errorhandling.SerializableNetworkError
import com.metacto.strapikmm.util.DESEncryption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class TesterClass {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val key = "u1BvOHzUOcklgNpn1MaWvdn9DT4LyzSX"
            val iv = "fsd23243"

            val des = DESEncryption(key, iv)

            // String to encrypt
            val plainText = "channel=myChannel&path=/round/2&referrerCustomerId=referrerCustomerId&referrerName=referrerName&refererID=refererID&campaign=campaign&baseDeepLink=baseDeepLink&deepLinkPath=deepLinkPath&referrerImageURL=referrerImageURL&jwt=12344"

            // Padding the input string
            // Encrypting the padded string
            val encrypted = des.encrypt(plainText)

            println("Encrypted: ${encrypted}")

            // Decrypting the encrypted string
            val decrypted = des.decrypt(encrypted)
            println("Decrypted: $decrypted")

            val map = mutableMapOf(
                "Message" to JsonPrimitive("An error has occurred."),
                "ExceptionMessage" to JsonPrimitive("No username found for token"),
                "ExceptionType" to JsonPrimitive("GlobalComponents.Data.Exceptions.NotFoundException"),
                "StackTrace" to JsonPrimitive(null)
            )
            val element = JsonObject(map)
        }
    }
}