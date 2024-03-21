package com.metaCTO.strapikmm.model.contact

import com.metaCTO.strapikmm.util.nullIfEmpty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
