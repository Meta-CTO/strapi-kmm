package com.swensonhe.strapikmm.model.contact

import com.swensonhe.strapikmm.util.nullIfEmpty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a contact with various attributes.
 *
 * @param firstName The first name of the contact.
 * @param lastName The last name of the contact.
 * @param phoneNumbers A list of phone numbers associated with the contact.
 * @param emailAddresses A list of email addresses associated with the contact.
 * @param middleName The middle name of the contact.
 * @param namePrefix The name prefix (e.g., Mr., Ms.) of the contact.
 * @param nameSuffix The name suffix (e.g., Jr., Sr.) of the contact.
 * @param birthday The birthday of the contact.
 * @param previousFamilyName The previous family name of the contact.
 * @param nickname The nickname of the contact.
 * @param postalAddresses A list of postal addresses associated with the contact.
 * @param urlAddresses A list of URL addresses associated with the contact.
 * @param instantMessageAddresses A list of instant message addresses associated with the contact.
 * @param socialProfiles A list of social profiles associated with the contact.
 * @param relations A list of relations to other contacts.
 * @param type The type of the contact (e.g., "person," "organization").
 * @param jobTitle The job title of the contact.
 * @param departmentName The department name of the contact.
 * @param organizationName The organization name of the contact.
 * @param isImageDataAvailable A flag indicating whether image data is available for the contact.
 * @param imageData The binary image data for the contact.
 * @param thumbnailImageData The binary thumbnail image data for the contact.
 */
@Serializable
data class Contact(
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("phoneNumbers")
    val phoneNumbers: List<String>,
    @SerialName("emailAddresses")
    val emailAddresses: List<String>,
    @SerialName("middleName")
    val middleName: String? = null,
    @SerialName("namePrefix")
    val namePrefix: String? = null,
    @SerialName("nameSuffix")
    val nameSuffix: String? = null,
    @SerialName("birthday")
    val birthday: String? = null,
    @SerialName("previousFamilyName")
    val previousFamilyName: String? = null,
    @SerialName("nickname")
    val nickname: String? = null,
    @SerialName("postalAddresses")
    val postalAddresses: List<String>,
    @SerialName("urlAddresses")
    val urlAddresses: List<String>,
    @SerialName("instantMessageAddresses")
    val instantMessageAddresses: List<String>,
    @SerialName("socialProfiles")
    val socialProfiles: List<String>,
    @SerialName("relations")
    val relations: List<String>,
    @SerialName("type")
    val type: String? = null,
    @SerialName("jobTitle")
    val jobTitle: String? = null,
    @SerialName("departmentName")
    val departmentName: String? = null,
    @SerialName("organizationName")
    val organizationName: String? = null,
    @SerialName("isImageDataAvailable")
    val isImageDataAvailable: Boolean,
    @SerialName("imageData")
    val imageData: ByteArray? = null,
    @SerialName("thumbnailImageData")
    val thumbnailImageData: ByteArray? = null
) {
    fun getName() = listOfNotNull(
        firstName?.nullIfEmpty(),
        lastName?.nullIfEmpty()
    ).joinToString(" ")

    fun getNameInitials() = listOfNotNull(
        firstName?.nullIfEmpty()?.firstOrNull(),
        lastName?.nullIfEmpty()?.firstOrNull()
    ).joinToString("")
}
