package com.kommu.mylibrary

import android.util.Log
import com.metaCTO.strapikmm.datasource.network.KmmBaseService
import com.metaCTO.strapikmm.datasource.network.StrapiRequestBuilder
import com.metaCTO.strapikmm.datasource.network.StrapiSortType
import com.metaCTO.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.metaCTO.strapikmm.datasource.network.services.strapi.convert
import com.metaCTO.strapikmm.sharedpreference.KmmPreference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class TesterClass {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Questions
            try {
                val json = Json.parseToJsonElement(
                    "{\n" +
                            "    \"data\": {\n" +
                            "        \"id\": 1,\n" +
                            "        \"attributes\": {\n" +
                            "            \"createdAt\": \"2022-06-14T17:46:48.114Z\",\n" +
                            "            \"updatedAt\": \"2023-04-28T07:30:01.542Z\",\n" +
                            "            \"waitlistSignUpRedirectURL\": \"https://www.thevellaapp.com/tester-confirmation/\",\n" +
                            "            \"privacyPolicy\": \"https://www.thevellaapp.com/privacy/\",\n" +
                            "            \"termsOfService\": \"https://www.thevellaapp.com/privacy/\",\n" +
                            "            \"defaultExperienceRangeInMiles\": null,\n" +
                            "            \"onboarding_personality\": {\n" +
                            "                \"data\": {\n" +
                            "                    \"id\": 2,\n" +
                            "                    \"attributes\": {\n" +
                            "                        \"title\": \"Extraversion\",\n" +
                            "                        \"description\": \"The age old question of \\\"Are you an Extrovert or Introvert\\\" is one that gets volleyed around rather frequently. We all have an idea of what the two labels mean, but we never really give the concept credit for being so complex and multilayered. \\n\\nExtraversion isn't just about how social you are. Think about it. Being social involves a whole host of layers: how motivated you are to be social, your specialized way of being social, how often you're social... you get the picture.\\n\\nExtraversion as a trait describes how often and with what quality you interact with external activities. Extraversion isn't meant to be a measure of how \\\"prosocial\\\" you are, or a way to describe whether or not you like people, so people who score lower on this trait shouldn't be seen as being \\\"asocial\\\" or \\\"antisocial\\\" at all. \\n\\nLow scorers of the extraversion trait may be called \\\"introverts\\\" and all that means is that they need less external stimulation, value the time they're able to have on their own, and may choose more inward-facing activities for self-care. \\n\\nHigh scorers in the extraversion trait may be seen as being more easily excitable, adventurous, pleasure-seeking and will likely prefer activities that provide high stimulation. \\n\\nPeople who fall in the middle will likely enjoy times that provide them with sufficient stimulation but know where their boundaries are, as well as when and how \\\"recharging\\\" is needed. \",\n" +
                            "                        \"high_value_title\": \"Extravert\",\n" +
                            "                        \"low_value_title\": \"Introvert\",\n" +
                            "                        \"createdAt\": \"2022-06-13T19:51:15.763Z\",\n" +
                            "                        \"updatedAt\": \"2022-12-13T16:11:50.097Z\",\n" +
                            "                        \"color_aarrggbb\": \"FFEC875E\",\n" +
                            "                        \"medium_value_title\": \"Ambivert\",\n" +
                            "                        \"test\": null,\n" +
                            "                        \"low_value_description\": null,\n" +
                            "                        \"medium_value_description\": null,\n" +
                            "                        \"high_value_description\": null,\n" +
                            "                        \"order\": 3\n" +
                            "                    }\n" +
                            "                }\n" +
                            "            },\n" +
                            "            \"permissions\": [\n" +
                            "                {\n" +
                            "                    \"id\": 1,\n" +
                            "                    \"title\": \"Bluetooth\",\n" +
                            "                    \"justification\": \"Please Grant Bluetooth Permission.\",\n" +
                            "                    \"type\": \"bluetooth\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 2,\n" +
                            "                    \"title\": \"Calendar\",\n" +
                            "                    \"justification\": \"The way you manage your calendar can tell us a lot about who you are. \\n\\nThat is why we will only look at things like:\\n\\n- How many events you have on your calendar\\n- How many events you have responded to\\n- The date and time of events\\n\\nWe do not look at or save your event details, attendee information etc.\",\n" +
                            "                    \"type\": \"calendar\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 3,\n" +
                            "                    \"title\": \"Contacts\",\n" +
                            "                    \"justification\": \"The way you organize your contact list can tell us a lot about who you are. \\n\\nThat is why we will only look at things like:\\n\\n- How you enter their information (first and last name filled in, first name only etc.)\\n- If you have profile pictures\\n- If you have addresses entered\\n\\nWe do not look at or save your contacts actual information.\",\n" +
                            "                    \"type\": \"contacts\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 4,\n" +
                            "                    \"title\": \"Spotify\",\n" +
                            "                    \"justification\": \"Music has very strong links to personality and mood. What we listen to, when and how often we listen to it are strong indicators.\\n\\nWe will look at things like\\n\\n- How long you listen to music\\n- When you listen (time of day)\\n- What kind of music (genre, artist, etc.)\\n\\nWe do not look at or save any Spotify account information.\",\n" +
                            "                    \"type\": \"spotify\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 5,\n" +
                            "                    \"title\": \"Gmail\",\n" +
                            "                    \"justification\": \"The way you manage your inbox can tell us a lot about who you are. \\n\\nThat is why we will only look at things like:\\n\\n- How many email you receive/send\\n- How long email are (character count)\\n- How many unread emails you have\\n- When you check your emails\\n\\nWe do not look at or save any of the email/senders information.\",\n" +
                            "                    \"type\": \"gmail\"\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"personality_ranges\": [\n" +
                            "                {\n" +
                            "                    \"id\": 1,\n" +
                            "                    \"key\": \"low\",\n" +
                            "                    \"min_value\": 0.1,\n" +
                            "                    \"max_value\": 2.2\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 2,\n" +
                            "                    \"key\": \"medium\",\n" +
                            "                    \"min_value\": 2.2,\n" +
                            "                    \"max_value\": 3.2\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 3,\n" +
                            "                    \"key\": \"high\",\n" +
                            "                    \"min_value\": 3.2,\n" +
                            "                    \"max_value\": 5\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"educational_levels\": [\n" +
                            "                {\n" +
                            "                    \"id\": 1,\n" +
                            "                    \"title\": \"Middle school\\t\",\n" +
                            "                    \"key\": \"middle_school\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 3,\n" +
                            "                    \"title\": \"High school diploma or GED\",\n" +
                            "                    \"key\": \"high_school\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 4,\n" +
                            "                    \"title\": \"Some college or 2-year degree\",\n" +
                            "                    \"key\": \"associates_degree\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 6,\n" +
                            "                    \"title\": \"4-year college graduate\",\n" +
                            "                    \"key\": \"bachelors_degree\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 5,\n" +
                            "                    \"title\": \"Graduate or professional degree\",\n" +
                            "                    \"key\": \"masters_degree\"\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"activityPercentiles\": [\n" +
                            "                {\n" +
                            "                    \"id\": 64912,\n" +
                            "                    \"numberOfActivities\": 1,\n" +
                            "                    \"percentage\": 0\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64914,\n" +
                            "                    \"numberOfActivities\": 1,\n" +
                            "                    \"percentage\": 5\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64913,\n" +
                            "                    \"numberOfActivities\": 1,\n" +
                            "                    \"percentage\": 10\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64915,\n" +
                            "                    \"numberOfActivities\": 1,\n" +
                            "                    \"percentage\": 15\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64916,\n" +
                            "                    \"numberOfActivities\": 2,\n" +
                            "                    \"percentage\": 20\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64917,\n" +
                            "                    \"numberOfActivities\": 2,\n" +
                            "                    \"percentage\": 25\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64918,\n" +
                            "                    \"numberOfActivities\": 2,\n" +
                            "                    \"percentage\": 30\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64919,\n" +
                            "                    \"numberOfActivities\": 2,\n" +
                            "                    \"percentage\": 35\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64921,\n" +
                            "                    \"numberOfActivities\": 3,\n" +
                            "                    \"percentage\": 40\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64920,\n" +
                            "                    \"numberOfActivities\": 3,\n" +
                            "                    \"percentage\": 45\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64922,\n" +
                            "                    \"numberOfActivities\": 4,\n" +
                            "                    \"percentage\": 50\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64923,\n" +
                            "                    \"numberOfActivities\": 5,\n" +
                            "                    \"percentage\": 55\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64924,\n" +
                            "                    \"numberOfActivities\": 5,\n" +
                            "                    \"percentage\": 60\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64925,\n" +
                            "                    \"numberOfActivities\": 6,\n" +
                            "                    \"percentage\": 65\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64926,\n" +
                            "                    \"numberOfActivities\": 6,\n" +
                            "                    \"percentage\": 70\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64927,\n" +
                            "                    \"numberOfActivities\": 9,\n" +
                            "                    \"percentage\": 75\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64928,\n" +
                            "                    \"numberOfActivities\": 11,\n" +
                            "                    \"percentage\": 80\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64929,\n" +
                            "                    \"numberOfActivities\": 12,\n" +
                            "                    \"percentage\": 85\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64930,\n" +
                            "                    \"numberOfActivities\": 14,\n" +
                            "                    \"percentage\": 90\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64931,\n" +
                            "                    \"numberOfActivities\": 19,\n" +
                            "                    \"percentage\": 95\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 64932,\n" +
                            "                    \"numberOfActivities\": 98,\n" +
                            "                    \"percentage\": 100\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"personalityQuestionsMinimumCount\": [\n" +
                            "                {\n" +
                            "                    \"id\": 1,\n" +
                            "                    \"count\": 10\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 2,\n" +
                            "                    \"count\": 10\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 3,\n" +
                            "                    \"count\": 10\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 4,\n" +
                            "                    \"count\": 10\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"id\": 5,\n" +
                            "                    \"count\": 10\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"journeyAssets\": []\n" +
                            "        }\n" +
                            "    },\n" +
                            "    \"meta\": {}\n" +
                            "}"
                )
                val flatee = JsonFlatter.flat<AppConfiguration>(json)
                println(flatee)
                val flat = flatee.convert<AppConfiguration>()

                val first = flat.educationLevels.orEmpty().first()
                val last = flat.educationLevels?.last()

                val strapiRequestBuilder = StrapiRequestBuilder()
                strapiRequestBuilder.endpoint("/missions")
                strapiRequestBuilder.strapiQueryBuilder {
                    sortBy("orderIndex", StrapiSortType.ASC)
                    sortBy("data", StrapiSortType.ASC)
                    sortBy("sdsddsw", StrapiSortType.DESC)
                    sortBy("sdfds", StrapiSortType.ASC)
                    groupBy("age")
                    groupBy("ds")
                    groupBy("ds")
                }

                val baseService = KmmBaseService(
                    "https://www.thevellaapp.com",
                    KmmPreference()
                )
                val httpRequest = baseService.buildRequest(strapiRequestBuilder, "GET")
                println(httpRequest)
                println(httpRequest.url)
                println(httpRequest.headers)
                println(httpRequest.body)
                println(httpRequest.method)
            } catch (throwable: Throwable) {
                Log.e("Error", throwable.message.orEmpty())
            }

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
open class StepStoneType(
    @SerialName("component")
    open val type: StepStoneComponent? = null,
)

@Serializable
enum class StepStoneComponent(val type: String) {
    @SerialName("journey.discover-personality")
    DISCOVER_PERSONALITY("journey.discover-personality"),

    @SerialName("journey.complete-goal")
    COMPLETE_GOAL("journey.complete-goal"),

    @SerialName("journey.discover-character")
    DISCOVER_CHARACTER("journey.discover-character"),

    @SerialName("journey.complete-activity")
    COMPLETE_ACTIVITY("journey.complete-activity");

    companion object {
        fun from(type: String): StepStoneComponent? {
            return values().firstOrNull { it.type == type }
        }
    }
}

@Serializable
data class StepStoneCompleteActivityType(
    @SerialName("id")
    val id: Int,
    @SerialName("__component")
    override val type: StepStoneComponent? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("dayRange")
    val dayRange: Int? = null
) : StepStoneType(type)

@Serializable
data class StepStoneCompleteGoalType(
    @SerialName("id")
    val id: Int,
    @SerialName("__component")
    override val type: StepStoneComponent? = null,
    @SerialName("minDifficulty")
    val minDifficulty: Int? = null,
    @SerialName("minCompletionRate")
    val minCompletionRate: Double? = null,
    @SerialName("quantity")
    val quantity: Int? = null,
    @SerialName("dayRange")
    val dayRange: Int? = null,
) : StepStoneType(type)

@Serializable
data class StepStoneDiscoverPersonalityType(
    @SerialName("id")
    val id: Int,
    @SerialName("__component")
    override val type: StepStoneComponent? = null,
) : StepStoneType(type)

@Serializable
data class StepStoneDiscoverCharacterType(
    @SerialName("id")
    val id: Int,
    @SerialName("__component")
    override val type: StepStoneComponent? = null,
    @SerialName("requireAll")
    val requireAll: Boolean? = null,
) : StepStoneType(type)

@Serializable
data class JourneyStepStone(
    @SerialName("id")
    val id: Int,
    @SerialName("attributes.title")
    val title: String? = null,
    @SerialName("attributes.description")
    val description: String? = null,
    @SerialName("attributes.requiredStep.data.id")
    val requiredStepId: Int? = null,
    @SerialName("attributes.requiredStep.data.attributes.title")
    val requiredStepTitle: String? = null,
    @SerialName("attributes.type")
    val type: JsonArray? = null
)

@Serializable
data class JourneyLevel(
    @SerialName("id")
    val id: Int,
    @SerialName("attributes.title")
    val title: String? = null,
    @SerialName("attributes.orderIndex")
    val orderIndex: Int? = null,
    @SerialName("attributes.color")
    val color: String? = null,
    @SerialName("attributes.previous.data.id")
    val previousLevelId: Int? = null,
    @SerialName("attributes.previous.data.attributes.title")
    val previousLevelTitle: String? = null,
    @SerialName("attributes.stepstones.data")
    val stepstones: List<JourneyStepStone>? = null,
)


//////    ===== App config for vella

@Serializable
data class AppConfiguration(
    @SerialName("data.id")
    val id: Int? = null,
    @SerialName("data.attributes.educational_levels")
    val educationLevels: List<EducationLevel>? = null,
    @SerialName("data.attributes.permissions")
    val permissions: List<AppConfigPermission>? = null,
    @SerialName("data.attributes.privacyPolicy")
    val privacyPolicy: String? = null,
    @SerialName("data.attributes.termsOfService")
    val termsOfService: String? = null,
    @SerialName("data.attributes.activityPercentiles")
    val activityPercentiles: List<ActivityPercentile>? = null,
    @SerialName("data.attributes.journeyAssets")
    val journeyAssets: List<JourneyAssets>? = null
)

@Serializable
data class JourneyAssets(
    @SerialName("stoneIcons")
    val stoneIcons: List<StoneIcon>?
)

@Serializable
data class StoneIcon(
    @SerialName("componentName")
    val componentName: String?,
)

@Serializable
class ActivityPercentile(
    @SerialName("id")
    val id: Int,
    @SerialName("numberOfActivities")
    val numberOfActivities: Int,
    @SerialName("percentage")
    val percentage: Int
)

@Serializable
class EducationLevel(
    @SerialName("id")
    val id: Int,
    @SerialName("key")
    val key: String,
    @SerialName("title")
    val title: String
)

@Serializable
class AppConfigPermission(
    @SerialName("id")
    val id: Int,
    @SerialName("justification")
    val justification: String,
    @SerialName("type")
    val type: PermissionType,
    @SerialName("title")
    val title: String
)

@Serializable
enum class PermissionType {
    @SerialName("bluetooth")
    Bluetooth,

    @SerialName("calendar")
    Calendar,

    @SerialName("contacts")
    Contacts,

    @SerialName("spotify")
    Spotify,

    @SerialName("gmail")
    Gmail,

    @SerialName("wifi")
    Wifi,

    @SerialName("battery")
    Battery,

    @SerialName("location")
    Location;
}
