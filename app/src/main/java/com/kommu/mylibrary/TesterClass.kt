package com.kommu.mylibrary

import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.datasource.network.services.strapi.convert
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.LocalDate
import kotlin.jvm.JvmStatic

class TesterClass {
    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            // Questions
            val json = Json.parseToJsonElement("{\n" +
                    "            \"id\": 581,\n" +
                    "            \"attributes\": {\n" +
                    "                \"receivedAt\": null,\n" +
                    "                \"createdAt\": \"2022-12-27T02:52:25.995Z\",\n" +
                    "                \"updatedAt\": \"2022-12-27T02:52:25.995Z\",\n" +
                    "                \"unreadCount\": null,\n" +
                    "                \"channelURL\": null,\n" +
                    "                \"destination\": {\n" +
                    "                    \"data\": {\n" +
                    "                        \"id\": 152,\n" +
                    "                        \"attributes\": {\n" +
                    "                            \"username\": \"alissajoseymoure\",\n" +
                    "                            \"email\": \"alissajoseymoure@gmail.com\",\n" +
                    "                            \"provider\": null,\n" +
                    "                            \"confirmed\": true,\n" +
                    "                            \"blocked\": false,\n" +
                    "                            \"phoneNumber\": \"+15176733128\",\n" +
                    "                            \"timeZone\": null,\n" +
                    "                            \"firstName\": \"Alissa\",\n" +
                    "                            \"lastName\": \"Seymoure\",\n" +
                    "                            \"age\": null,\n" +
                    "                            \"gender\": null,\n" +
                    "                            \"firebaseUserID\": \"jZqPIU4ot6OuwLqcHn21javGk3m2\",\n" +
                    "                            \"appleEmail\": null,\n" +
                    "                            \"alternateEmail\": null,\n" +
                    "                            \"enableNotifications\": null,\n" +
                    "                            \"createdAt\": \"2022-12-27T02:48:23.117Z\",\n" +
                    "                            \"updatedAt\": \"2023-01-20T02:32:03.678Z\",\n" +
                    "                            \"instagram\": \"Alissajosey\",\n" +
                    "                            \"linkedIn\": \"\",\n" +
                    "                            \"referralCode\": \"3UAAQXXP\",\n" +
                    "                            \"properties\": {\n" +
                    "                                \"data\": [\n" +
                    "                                    {\n" +
                    "                                        \"id\": 63,\n" +
                    "                                        \"attributes\": {\n" +
                    "                                            \"title\": null,\n" +
                    "                                            \"nightlyCost\": 150,\n" +
                    "                                            \"cleaningCost\": 200,\n" +
                    "                                            \"description\": \"Super cute luxury convertible studio apartment on the border of Old Town and Gold Coast. All the high end amenities you can dream of (gym, peloton, steam room, sauna, work + entertainment spaces) and lots of good vibes in my unit. \",\n" +
                    "                                            \"isPrimaryResidence\": true,\n" +
                    "                                            \"createdAt\": \"2023-01-11T23:21:52.805Z\",\n" +
                    "                                            \"updatedAt\": \"2023-01-11T23:26:10.087Z\",\n" +
                    "                                            \"photos\": {\n" +
                    "                                                \"data\": [\n" +
                    "                                                    {\n" +
                    "                                                        \"id\": 350,\n" +
                    "                                                        \"attributes\": {\n" +
                    "                                                            \"name\": \"D16B6670-05DD-42C3-B8FA-36447E049790\",\n" +
                    "                                                            \"alternativeText\": \"D16B6670-05DD-42C3-B8FA-36447E049790\",\n" +
                    "                                                            \"caption\": \"D16B6670-05DD-42C3-B8FA-36447E049790\",\n" +
                    "                                                            \"width\": 0,\n" +
                    "                                                            \"height\": 0,\n" +
                    "                                                            \"formats\": null,\n" +
                    "                                                            \"hash\": \"D16B6670-05DD-42C3-B8FA-36447E049790\",\n" +
                    "                                                            \"ext\": \"jpeg\",\n" +
                    "                                                            \"mime\": \"image/jpeg\",\n" +
                    "                                                            \"size\": 143.38,\n" +
                    "                                                            \"url\": \"https://kommu-prod-upload.s3.us-west-2.amazonaws.com/D16B6670-05DD-42C3-B8FA-36447E049790_1DCF72E7-127E-411B-A25C-E5BC5464318D.jpeg\",\n" +
                    "                                                            \"previewUrl\": null,\n" +
                    "                                                            \"provider\": \"aws-s3\",\n" +
                    "                                                            \"provider_metadata\": null,\n" +
                    "                                                            \"createdAt\": \"2023-01-11T23:21:52.154Z\",\n" +
                    "                                                            \"updatedAt\": \"2023-01-11T23:21:52.154Z\"\n" +
                    "                                                        }\n" +
                    "                                                    },\n" +
                    "                                                    {\n" +
                    "                                                        \"id\": 351,\n" +
                    "                                                        \"attributes\": {\n" +
                    "                                                            \"name\": \"8921C868-1B0E-4D55-8CD2-D7F4729B3AA9\",\n" +
                    "                                                            \"alternativeText\": \"8921C868-1B0E-4D55-8CD2-D7F4729B3AA9\",\n" +
                    "                                                            \"caption\": \"8921C868-1B0E-4D55-8CD2-D7F4729B3AA9\",\n" +
                    "                                                            \"width\": 0,\n" +
                    "                                                            \"height\": 0,\n" +
                    "                                                            \"formats\": null,\n" +
                    "                                                            \"hash\": \"8921C868-1B0E-4D55-8CD2-D7F4729B3AA9\",\n" +
                    "                                                            \"ext\": \"jpeg\",\n" +
                    "                                                            \"mime\": \"image/jpeg\",\n" +
                    "                                                            \"size\": 193.54,\n" +
                    "                                                            \"url\": \"https://kommu-prod-upload.s3.us-west-2.amazonaws.com/8921C868-1B0E-4D55-8CD2-D7F4729B3AA9_5793AFD7-66D4-4322-80C6-2A23AEBE018E.jpeg\",\n" +
                    "                                                            \"previewUrl\": null,\n" +
                    "                                                            \"provider\": \"aws-s3\",\n" +
                    "                                                            \"provider_metadata\": null,\n" +
                    "                                                            \"createdAt\": \"2023-01-11T23:21:52.154Z\",\n" +
                    "                                                            \"updatedAt\": \"2023-01-11T23:21:52.154Z\"\n" +
                    "                                                        }\n" +
                    "                                                    }\n" +
                    "                                                ]\n" +
                    "                                            },\n" +
                    "                                            \"location\": {\n" +
                    "                                                \"id\": 506,\n" +
                    "                                                \"address1\": \"228 West Hill Street\",\n" +
                    "                                                \"address2\": \"\",\n" +
                    "                                                \"city\": \"Chicago\",\n" +
                    "                                                \"state\": \"IL\",\n" +
                    "                                                \"zip\": \"60610\",\n" +
                    "                                                \"placeId\": \"ChIJLf-PTq_TD4gRjJTWIsyuqQ4\",\n" +
                    "                                                \"lat\": 41.9,\n" +
                    "                                                \"long\": -87.64\n" +
                    "                                            },\n" +
                    "                                            \"propertyType\": {\n" +
                    "                                                \"data\": {\n" +
                    "                                                    \"id\": 1,\n" +
                    "                                                    \"attributes\": {\n" +
                    "                                                        \"title\": \"Entire Home\",\n" +
                    "                                                        \"createdAt\": \"2022-11-04T00:40:51.628Z\",\n" +
                    "                                                        \"updatedAt\": \"2022-11-04T00:40:51.628Z\"\n" +
                    "                                                    }\n" +
                    "                                                }\n" +
                    "                                            },\n" +
                    "                                            \"availabilities\": {\n" +
                    "                                                \"data\": [\n" +
                    "                                                    {\n" +
                    "                                                        \"id\": 93,\n" +
                    "                                                        \"attributes\": {\n" +
                    "                                                            \"startDate\": \"2023-01-08\",\n" +
                    "                                                            \"endDate\": \"2023-02-09\",\n" +
                    "                                                            \"isFlexible\": false,\n" +
                    "                                                            \"createdAt\": \"2023-01-11T23:25:31.287Z\",\n" +
                    "                                                            \"updatedAt\": \"2023-01-11T23:25:31.287Z\"\n" +
                    "                                                        }\n" +
                    "                                                    },\n" +
                    "                                                    {\n" +
                    "                                                        \"id\": 94,\n" +
                    "                                                        \"attributes\": {\n" +
                    "                                                            \"startDate\": \"2023-03-14\",\n" +
                    "                                                            \"endDate\": \"2023-03-23\",\n" +
                    "                                                            \"isFlexible\": false,\n" +
                    "                                                            \"createdAt\": \"2023-01-11T23:27:25.261Z\",\n" +
                    "                                                            \"updatedAt\": \"2023-01-11T23:27:25.261Z\"\n" +
                    "                                                        }\n" +
                    "                                                    }\n" +
                    "                                                ]\n" +
                    "                                            }\n" +
                    "                                        }\n" +
                    "                                    }\n" +
                    "                                ]\n" +
                    "                            },\n" +
                    "                            \"image\": {\n" +
                    "                                \"data\": {\n" +
                    "                                    \"id\": 229,\n" +
                    "                                    \"attributes\": {\n" +
                    "                                        \"name\": \"AA36D8B8-F29C-4A00-97F5-4CCEFC3CC5EE\",\n" +
                    "                                        \"alternativeText\": \"AA36D8B8-F29C-4A00-97F5-4CCEFC3CC5EE\",\n" +
                    "                                        \"caption\": \"AA36D8B8-F29C-4A00-97F5-4CCEFC3CC5EE\",\n" +
                    "                                        \"width\": 0,\n" +
                    "                                        \"height\": 0,\n" +
                    "                                        \"formats\": null,\n" +
                    "                                        \"hash\": \"AA36D8B8-F29C-4A00-97F5-4CCEFC3CC5EE\",\n" +
                    "                                        \"ext\": \"jpeg\",\n" +
                    "                                        \"mime\": \"image/jpeg\",\n" +
                    "                                        \"size\": 58.91,\n" +
                    "                                        \"url\": \"https://kommu-prod-upload.s3.us-west-2.amazonaws.com/AA36D8B8-F29C-4A00-97F5-4CCEFC3CC5EE_F32ECEED-644B-4CB0-BA97-72C16F45DD54.jpeg\",\n" +
                    "                                        \"previewUrl\": null,\n" +
                    "                                        \"provider\": \"aws-s3\",\n" +
                    "                                        \"provider_metadata\": null,\n" +
                    "                                        \"createdAt\": \"2022-12-27T02:51:21.033Z\",\n" +
                    "                                        \"updatedAt\": \"2022-12-27T02:51:21.033Z\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            \"location\": {\n" +
                    "                                \"id\": 364,\n" +
                    "                                \"address1\": \"\",\n" +
                    "                                \"address2\": \"\",\n" +
                    "                                \"city\": \"Chicago\",\n" +
                    "                                \"state\": \"IL\",\n" +
                    "                                \"zip\": \"\",\n" +
                    "                                \"placeId\": \"ChIJ7cv00DwsDogRAMDACa2m4K8\",\n" +
                    "                                \"lat\": 41.88,\n" +
                    "                                \"long\": -87.63\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }")
            val flatee = JsonFlatter.flat<Friend>(json)
            println(flatee)
            val flat = flatee.convert<Friend>()

            val unreadCount = flat.unreadCount
            val user = flat.user

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
data class Friend(
    @JsonNames("unreadCount")
    val unreadCount: Int?,
    @JsonNames( "attributes.destination", "attributes.destination.data", "destination")
    val user: User
)

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @JsonNames("firstName", "attributes.firstName")
    val firstName: String? = null,
    @JsonNames("lastName", "attributes.lastName")
    val lastName: String? = null,
    @JsonNames("phoneNumber", "attributes.phoneNumber")
    val phoneNumber: String? = null,
    @JsonNames("email", "attributes.email")
    val email: String? = null,
    @JsonNames("location", "attributes.location")
    val location: Address? = null,
    @JsonNames("instagram", "attributes.instagram")
    val instagram: String? = null,
    @JsonNames("linkedIn", "attributes.linkedIn")
    val linkedIn: String? = null,
    @JsonNames("image", "attributes.image", "attributes.image.data")
    val image: File? = null,
    @JsonNames("properties", "attributes.properties", "attributes.properties.data")
    val properties: List<Property>?,
    @JsonNames("referralCode", "attributes.referralCode")
    val referralCode: String? = null
) {

    val fullName: String
        get() {
            return buildString {
                append(firstName ?: "")
                if (lastName != null) {
                    append(" $lastName")
                }
            }
        }

    val onboardingState: OnboardingState
        get() {
            if (phoneNumber == null) {
                return OnboardingState.MISSING_PHONE_NUMBER
            }

            if (image == null) {
                return OnboardingState.MISSING_PROFILE_PICTURE
            }

            return OnboardingState.COMPLETE
        }

    val propertyId: Int?
        get() {
            return properties?.firstOrNull()?.id
        }
}

@Serializable
data class Property(
    @SerialName("id")
    val id: Int,
    @JsonNames("nightlyCost", "attributes.nightlyCost")
    val nightlyCost: Double?,
    @JsonNames("cleaningCost", "attributes.cleaningCost")
    val cleaningCost: Double?,
    @JsonNames("description", "attributes.description")
    val description: String?,
    @JsonNames("propertyType", "attributes.propertyType", "attributes.propertyType.data")
    val type: PropertyType?,
    @JsonNames("photos", "attributes.photos", "attributes.photos.data")
    val photos: List<File>?,
    @JsonNames("location", "attributes.location", "attributes.location.data")
    val location: Address?,
    @JsonNames("availabilities", "attributes.availabilities", "attributes.availabilities.data")
    val availabilities: List<Availability>?,
    @JsonNames("user", "attributes.user", "attributes.user.data")
    val user: User?
)

@Serializable
data class Availability(
    @SerialName("id")
    val id: Int,
    @JsonNames("isFlexible", "attributes.isFlexible")
    val isFlexible: Boolean?,
    @JsonNames("guest.data", "attributes.guest.data")
    val guest: User?
) {

    val updatingDueToScheduledGuest: Boolean
        get() {
            return guest != null
        }
}

@Serializable
data class PropertyType(
    @SerialName("id")
    val id: Int,
    @JsonNames("title", "attributes.title")
    val title: String?
)

@Serializable
data class File(
    @JsonNames("id")
    val id: Int? = null,
    @JsonNames("attributes.url", "url")
    val url: String? = null,
    @JsonNames("attributes.name", "name")
    val name: String? = null,
    @JsonNames("attributes.mime", "mime")
    val mime: String? = null
)

@Serializable
data class Address(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("address1")
    val address1: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("address2")
    val address2: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("city")
    val city: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("state")
    val state: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("zip")
    val zip: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("placeId")
    val placeId: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("lat")
    val lat: Double? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("long")
    val long: Double? = null
)

enum class OnboardingState(val key: String) {
    MISSING_PHONE_NUMBER("missingPhoneNumber"),
    MISSING_PROFILE_PICTURE("missingProfilePicture"),
    COMPLETE("complete")
}
