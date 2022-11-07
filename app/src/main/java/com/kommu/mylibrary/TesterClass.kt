package com.kommu.mylibrary

import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.datasource.network.services.strapi.convert
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.jvm.JvmStatic

class TesterClass {
    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            val map = mutableMapOf<String, JsonElement>()
            val data = mutableMapOf<String, JsonElement>()
            data.put("id", JsonPrimitive(80))

            val attributes = mutableMapOf<String, JsonElement>()
            attributes.put("title", JsonPrimitive("ww"))
            attributes.put("description", JsonPrimitive("contractor description"))
            attributes.put("websiteURL", JsonPrimitive("google.com"))

            val service = mutableMapOf<String, JsonElement>()
            service.put("id", JsonPrimitive(32))
            val serviceAttributes = mutableMapOf<String, JsonElement>()
            serviceAttributes.put("title", JsonPrimitive("sadfsfds"))

            service.put("attributes", JsonObject(serviceAttributes))


            val servicesData = JsonArray(JsonArray(listOf(JsonObject(service))))
            val servicesObject = mutableMapOf<String, JsonElement>()
            servicesObject.put("data", servicesData)

            attributes.put("services", JsonObject(servicesObject))

            data.put("attributes", JsonObject(attributes))
            map.put("data", JsonObject(data))
            val json = JsonObject(map)
            val flat = JsonFlatter.flat<DataWrapper<ContractorData>>(json).convert<DataWrapper<ContractorData>>()
            val title = flat.data.title
            val id = flat.data.id
            val description = flat.data.description

            println(description)

        }
    }
}

@Serializable
data class DataWrapper<T>(
    @SerialName("data")
    val data: T
)

@Serializable
data class ContractorData(
    @JsonNames("attributes.description", "description")
    val description: String? = null,
    @JsonNames("attributes.rating", "rating")
    val rating: Double? = null,
    @JsonNames("attributes.email", "email")
    val email: String? = null,
    @JsonNames("attributes.title", "title")
    val title: String,
    @JsonNames("attributes.websiteURL", "websiteURL")
    val websiteURL: String? = null,
    @JsonNames("attributes.phoneNumber", "phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @JsonNames("attributes.serviceAreas.data", "serviceAreas")
    val serviceAreas: List<ServiceArea>? = null,
    @JsonNames("attributes.services.data", "services")
    val services: List<Service>? = null,
    @JsonNames("attributes.address", "address")
    val address: Address? = null,
    @JsonNames("attributes.logo.data.attributes.url", "logo.url")
    val imageUrl: String? = null,
    @JsonNames("attributes.logo.data.id", "logo.id")
    val imageId: Int? = null,
)

@Serializable
data class ServiceArea(
    @SerialName("title")
    val title: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("id")
    val id: Int? = null,
)

@Serializable
data class Service(
    @SerialName("attributes.title")
    val title: String? = null,
    @SerialName("id")
    val id: Int? = null,
)

@Serializable
data class Address(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("street1")
    val street1: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("street2")
    val street2: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("city")
    val city: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("state")
    val state: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("zip")
    val zip: String? = null
) {
    fun getFullAddress() = listOfNotNull(street1, street2, city, state).joinToString(", ")
}
