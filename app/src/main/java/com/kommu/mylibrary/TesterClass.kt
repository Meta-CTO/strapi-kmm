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
            // Questions
            val json = Json.parseToJsonElement("{\n" +
                    "    \"data\": [\n" +
                    "        {\n" +
                    "            \"id\": 122,\n" +
                    "            \"attributes\": {\n" +
                    "                \"priority\": 3,\n" +
                    "                \"title\": \"Building that Represents You\",\n" +
                    "                \"high_value_title\": null,\n" +
                    "                \"low_value_title\": null,\n" +
                    "                \"enabled\": true,\n" +
                    "                \"skippable\": true,\n" +
                    "                \"description\": null,\n" +
                    "                \"is_onboarding_question\": true,\n" +
                    "                \"personality_relationship\": \"inverted\",\n" +
                    "                \"weight\": 1,\n" +
                    "                \"createdAt\": \"2022-12-12T16:52:40.301Z\",\n" +
                    "                \"updatedAt\": \"2022-12-12T17:06:21.068Z\",\n" +
                    "                \"type\": \"image\",\n" +
                    "                \"choices\": [\n" +
                    "                    {\n" +
                    "                        \"id\": 8,\n" +
                    "                        \"choice\": null,\n" +
                    "                        \"title\": null,\n" +
                    "                        \"description\": null,\n" +
                    "                        \"branchID\": {\n" +
                    "                            \"data\": null\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                ],\n" +
                    "                \"images\": [\n" +
                    "                    {\n" +
                    "                        \"id\": 6,\n" +
                    "                        \"title\": null,\n" +
                    "                        \"description\": null,\n" +
                    "                        \"image\": {\n" +
                    "                            \"data\": {\n" +
                    "                                \"id\": 102,\n" +
                    "                                \"attributes\": {\n" +
                    "                                    \"name\": \"Cairo_opera_house.jpg\",\n" +
                    "                                    \"alternativeText\": \"Cairo_opera_house.jpg\",\n" +
                    "                                    \"caption\": \"Cairo_opera_house.jpg\",\n" +
                    "                                    \"width\": 800,\n" +
                    "                                    \"height\": 600,\n" +
                    "                                    \"formats\": {\n" +
                    "                                        \"small\": {\n" +
                    "                                            \"ext\": \".jpg\",\n" +
                    "                                            \"url\": \"https://fore-dev-upload.s3.us-west-2.amazonaws.com/small_Cairo_opera_house_94b83262cc.jpg\",\n" +
                    "                                            \"hash\": \"small_Cairo_opera_house_94b83262cc\",\n" +
                    "                                            \"mime\": \"image/jpeg\",\n" +
                    "                                            \"name\": \"small_Cairo_opera_house.jpg\",\n" +
                    "                                            \"path\": null,\n" +
                    "                                            \"size\": 25.48,\n" +
                    "                                            \"width\": 500,\n" +
                    "                                            \"height\": 375\n" +
                    "                                        },\n" +
                    "                                        \"medium\": {\n" +
                    "                                            \"ext\": \".jpg\",\n" +
                    "                                            \"url\": \"https://fore-dev-upload.s3.us-west-2.amazonaws.com/medium_Cairo_opera_house_94b83262cc.jpg\",\n" +
                    "                                            \"hash\": \"medium_Cairo_opera_house_94b83262cc\",\n" +
                    "                                            \"mime\": \"image/jpeg\",\n" +
                    "                                            \"name\": \"medium_Cairo_opera_house.jpg\",\n" +
                    "                                            \"path\": null,\n" +
                    "                                            \"size\": 48.65,\n" +
                    "                                            \"width\": 750,\n" +
                    "                                            \"height\": 563\n" +
                    "                                        },\n" +
                    "                                        \"thumbnail\": {\n" +
                    "                                            \"ext\": \".jpg\",\n" +
                    "                                            \"url\": \"https://fore-dev-upload.s3.us-west-2.amazonaws.com/thumbnail_Cairo_opera_house_94b83262cc.jpg\",\n" +
                    "                                            \"hash\": \"thumbnail_Cairo_opera_house_94b83262cc\",\n" +
                    "                                            \"mime\": \"image/jpeg\",\n" +
                    "                                            \"name\": \"thumbnail_Cairo_opera_house.jpg\",\n" +
                    "                                            \"path\": null,\n" +
                    "                                            \"size\": 6.21,\n" +
                    "                                            \"width\": 208,\n" +
                    "                                            \"height\": 156\n" +
                    "                                        }\n" +
                    "                                    },\n" +
                    "                                    \"hash\": \"Cairo_opera_house_94b83262cc\",\n" +
                    "                                    \"ext\": \".jpg\",\n" +
                    "                                    \"mime\": \"image/jpeg\",\n" +
                    "                                    \"size\": 51.11,\n" +
                    "                                    \"url\": \"https://fore-dev-upload.s3.us-west-2.amazonaws.com/Cairo_opera_house_94b83262cc.jpg\",\n" +
                    "                                    \"previewUrl\": null,\n" +
                    "                                    \"provider\": \"aws-s3\",\n" +
                    "                                    \"provider_metadata\": null,\n" +
                    "                                    \"createdAt\": \"2022-11-02T18:51:00.612Z\",\n" +
                    "                                    \"updatedAt\": \"2022-11-02T18:51:18.461Z\"\n" +
                    "                                }\n" +
                    "                            }\n" +
                    "                        },\n" +
                    "                        \"branchID\": {\n" +
                    "                            \"data\": null\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"meta\": {\n" +
                    "        \"pagination\": {\n" +
                    "            \"page\": 1,\n" +
                    "            \"pageSize\": 25,\n" +
                    "            \"pageCount\": 1,\n" +
                    "            \"total\": 10\n" +
                    "        }\n" +
                    "    }\n" +
                    "}")
            val flatee = JsonFlatter.flat<PagingResponse<InventoryQuestion>>(json)
            println(flatee)
            val flat = flatee.convert<PagingResponse<InventoryQuestion>>()

            val images = flat.data.get(0).images
            val id = flat.data.get(0).id

//            // Contractor
//            val map = mutableMapOf<String, JsonElement>()
//            val data = mutableMapOf<String, JsonElement>()
//            data.put("id", JsonPrimitive(80))
//
//            val attributes = mutableMapOf<String, JsonElement>()
//            attributes.put("title", JsonPrimitive("ww"))
//            attributes.put("description", JsonPrimitive("contractor description"))
//            attributes.put("websiteURL", JsonPrimitive("google.com"))
//
//            val service = mutableMapOf<String, JsonElement>()
//            service.put("id", JsonPrimitive(32))
//            val serviceAttributes = mutableMapOf<String, JsonElement>()
//            serviceAttributes.put("title", JsonPrimitive("sadfsfds"))
//
//            service.put("attributes", JsonObject(serviceAttributes))
//
//
//            val servicesData = JsonArray(JsonArray(listOf(JsonObject(service))))
//            val servicesObject = mutableMapOf<String, JsonElement>()
//            servicesObject.put("data", servicesData)
//
//            attributes.put("services", JsonObject(servicesObject))
//
//            data.put("attributes", JsonObject(attributes))
//            map.put("data", JsonObject(data))
//
//
////            val json = JsonObject(map)
//
//            val json = Json.parseToJsonElement("{\"data\":{\"id\":43,\"attributes\":{\"title\":\"3b3al Contractor\",\"description\":\"jkkjjklkj\",\"websiteURL\":null,\"rating\":null,\"createdAt\":\"2022-10-13T09:48:54.536Z\",\"updatedAt\":\"2022-11-04T19:33:03.767Z\",\"email\":\"shamyyoun+cont1@gmail.com\",\"phoneNumber\":\"2125458525\",\"proposedFirstName\":null,\"proposedLastName\":null,\"logo\":{\"data\":{\"id\":1132,\"attributes\":{\"name\":\"null\",\"alternativeText\":\"null\",\"caption\":\"null\",\"width\":0,\"height\":0,\"formats\":null,\"hash\":\"null\",\"ext\":\"jpg\",\"mime\":\"image/*\",\"size\":0.01,\"url\":\"https://dev-cozo-uploads.s3.us-west-2.amazonaws.com/6cc24461-1470-4487-a782-d3ef3269b2d2.jpg\",\"previewUrl\":null,\"provider\":\"aws-s3\",\"provider_metadata\":null,\"createdAt\":\"2022-11-03T19:49:40.259Z\",\"updatedAt\":\"2022-11-03T19:49:40.259Z\"}}},\"address\":{\"id\":490,\"street1\":\"asdas\",\"street2\":null,\"city\":\"asdasd\",\"state\":\"CO\",\"zip\":\"88888\",\"googleId\":null,\"lat\":null,\"lon\":null},\"serviceAreas\":[{\"id\":316,\"title\":\"55555\"}],\"services\":{\"data\":[{\"id\":15,\"attributes\":{\"title\":\"Bathrooms\",\"priority\":100,\"createdAt\":\"2022-10-25T17:13:11.621Z\",\"updatedAt\":\"2022-10-25T17:13:11.621Z\"}},{\"id\":18,\"attributes\":{\"title\":\"Painting\",\"priority\":100,\"createdAt\":\"2022-10-25T17:13:36.008Z\",\"updatedAt\":\"2022-10-25T17:13:36.008Z\"}},{\"id\":20,\"attributes\":{\"title\":\"Finished Carpentry\",\"priority\":100,\"createdAt\":\"2022-10-25T17:13:56.974Z\",\"updatedAt\":\"2022-10-25T17:13:56.974Z\"}},{\"id\":21,\"attributes\":{\"title\":\"Concrete\",\"priority\":100,\"createdAt\":\"2022-10-25T17:14:10.300Z\",\"updatedAt\":\"2022-10-25T17:14:10.300Z\"}}]}}},\"meta\":{}}")
//            val flatee = JsonFlatter.flat<DataWrapper<ContractorData>>(json)
//            println(flatee)
//            val flat = flatee.convert<DataWrapper<ContractorData>>()
//
//            val title = flat.data.title
//            val services = flat.data.services
//            val id = flat.data.id
//            val description = flat.data.description
//
//            println(description)
//            println(services)
//
//            println(description)



            // User

//            val map = mutableMapOf<String, JsonElement>()
//            val data = mutableMapOf<String, JsonElement>()
//            data.put("jwt", JsonPrimitive("jwt"))
//
//            val user = mutableMapOf<String, JsonElement>()
//            user.put("id", JsonPrimitive(22))
//            user.put("username", JsonPrimitive("username"))
//            user.put("timeZone", JsonPrimitive("timeZone"))
//            user.put("phoneNumber", JsonPrimitive("phoneNumber"))
//            user.put("provider", JsonPrimitive("provider"))
//
//            data.put("user", JsonObject(user))
//            map.put("data", JsonObject(data))
//            val json = JsonObject(map)
//
//            val flatee = JsonFlatter.flat<DataWrapper<AuthResponse>>(json)
//            println(flatee)
//                val flat = flatee.convert<DataWrapper<AuthResponse>>()
//            val jwt = flat.data.jwt
//            val username = flat.data.user.username
//            val id = flat.data.user.id
//
//            println(id)
//            println(username)

//            println(jwt)

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
    @JsonNames("attributes.services", "attributes.services.data", "services")
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


@Serializable
data class AuthResponse(
    @SerialName("user")
    val user: User,
    @SerialName("jwt")
    val jwt: String?,
)

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @JsonNames("attributes.username", "username")
    val username: String? = null,
    @JsonNames("attributes.timeZone", "timeZone")
    val timeZone: String? = null,
    @JsonNames("attributes.phoneNumber", "phoneNumber")
    val phoneNumber: String? = null,
    @JsonNames("attributes.provider", "provider")
    val provider: String? = null,
)


@Serializable
class InventoryQuestion(
    @SerialName("id")
    val id: Int? = null,
//    @JsonNames("attributes.title", "title")
//    val title: String? = null,
//    @JsonNames("attributes.description", "description")
//    val description: String? = null,
//    @JsonNames("attributes.high_value_title", "high_value_title")
//    val highValueTitle: String? = null,
//    @JsonNames("attributes.low_value_title", "low_value_title")
//    val lowValueTitle: String? = null,
//    @JsonNames("attributes.medium_value_title", "medium_value_title")
//    val mediumValueTitle: String? = null,
//    @JsonNames("attributes.enabled", "enabled")
//    val enabled: Boolean? = null,
//    @JsonNames("attributes.skippable", "skippable")
//    val skippable: Boolean? = null,
//    @JsonNames("attributes.is_onboarding_question", "is_onboarding_question")
//    val isOnBoardingQuestion: Boolean? = null,
//    @JsonNames("attributes.weight", "weight")
//    val weight: Double? = null,
//    @JsonNames("attributes.personality_relationship", "personality_relationship")
//    val personalityRelationship: String? = null,
//    @JsonNames("attributes.type", "type")
//    val type: InventoryQuestionType? = null,
//    @JsonNames("attributes.choices", "choices")
//    val choices: List<InventoryQuestionChoice>? = null,
    @JsonNames("attributes.images", "images")
    val images: List<InventoryQuestionImages>? = null,
)

//@Serializable
//class InventoryQuestionChoice(
//    @SerialName("id")
//    val id: Int? = null,
//    @SerialName("title")
//    val title: String? = null,
//    @SerialName("description")
//    val description: String? = null,
//    @SerialName("choice")
//    val choice: String? = null,
//    @JsonNames("branchID.data.id", "branchID.id")
//    val branchID: Int? = null,
//)

@Serializable
class InventoryQuestionImages(
    @JsonNames("image.data.attributes.url", "image.attributes.url")
    val image: String? = null,
)

@Serializable
enum class InventoryQuestionType {
    @SerialName("personality")
    PERSONALITY,
    @SerialName("choice")
    CHOICE,
    @SerialName("image")
    IMAGE,
}