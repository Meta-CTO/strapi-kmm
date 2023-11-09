package com.swensonhe.kmmsample

import kotlinx.serialization.json.JsonObject
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.swensonhe.strapikmm.datasource.network.services.strapi.containsKeyPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.jvm.JvmStatic

class TesterClass {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val jsonSample1 = "{" +
                    "    \"user\": {" +
                    "        \"profile\": {" +
                    "            \"name\": \"John\"," +
                    "            \"email\": \"ddf2dsfsfs@sfsf.fs\"" +
                    "        }" +
                    "    }" +
                    "}"


            val jsonSample2 = "{" +
                    "    \"company\": {" +
                    "        \"name\": \"Acme Inc.\"," +
                    "        \"employees\": {" +
                    "            \"employee1\": {" +
                    "                \"name\": \"Alice\"," +
                    "                \"email\": \"dsf2asf2saf2@fgeffsd.rff\"" +
                    "            }," +
                    "            \"employee2\": {" +
                    "                \"name\": \"Bob\"," +
                    "                \"email\": \"dsfsf@sdfsffs.sf\"" +
                    "            }" +
                    "        }" +
                    "    }" +
                    "}"

            val jsonSample3 = "{" +
                    "    \"data\": {" +
                    "        \"value\": 42" +
                    "    }" +
                    "}"

            val json = JsonWithIgnoredUnknownKeys.decodeFromString<JsonObject>(jsonSample1)
            val json2 = JsonWithIgnoredUnknownKeys.decodeFromString<JsonObject>(jsonSample2)
            val json3 = JsonWithIgnoredUnknownKeys.decodeFromString<JsonObject>(jsonSample3)


            val hasKey = json.containsKeyPath("user.profile.email")
            println("Key exists: $hasKey") // Should print "Key exists: true"

            val hasKey2 = json2.containsKeyPath("company.employees.employee1.email")
            println("Key exists: $hasKey2") // Should print "Key exists: true"

            val hasKey3 = json3.containsKeyPath("data.value")
            println("Key exists: $hasKey3") // Should print "Key exists: true"

            val hasKey4 = json3.containsKeyPath("data.value2")
            println("Key exists: $hasKey4") // Should print "Key exists: false"

            val hasKey5 = json3.containsKeyPath("data.value2.value3")
            println("Key exists: $hasKey5") // Should print "Key exists: false"

            val hasKey6 = json2.containsKeyPath("company.employees.employee1.email2")
            println("Key exists: $hasKey6") // Should print "Key exists: false"

            val hasKey7 = json2.containsKeyPath("company.employees.employee1.email2.email3")
            println("Key exists: $hasKey7") // Should print "Key exists: false"

            val hasKey8 = json2.containsKeyPath("company.employees.employee1.email2.email3.email4")
            println("Key exists: $hasKey8") // Should print "Key exists: false"
        }
    }
}

@Serializable
data class PagingResponse<T>(
    @SerialName("data")
    val data: List<T>
)

@Serializable
data class DataWrapper<T>(
    @SerialName("data")
    val data: T
)
