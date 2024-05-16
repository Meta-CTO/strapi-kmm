package com.kommu.mylibrary

import com.metacto.strapikmm.errorhandling.createErrorJsonResponse
import com.metacto.strapikmm.util.DESEncryption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

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
