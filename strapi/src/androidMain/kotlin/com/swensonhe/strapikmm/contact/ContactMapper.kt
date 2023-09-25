package com.swensonhe.strapikmm.contact

import android.content.Context
import android.net.Uri
import com.alexstyl.contactstore.Label
import com.alexstyl.contactstore.getLocalizedString
import com.alexstyl.contactstore.thumbnailUri
import com.swensonhe.strapikmm.contact.models.ContactImAddress
import com.swensonhe.strapikmm.contact.models.ContactPostalAddress
import com.swensonhe.strapikmm.contact.models.ContactRelation
import com.swensonhe.strapikmm.util.to2DigitsFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import com.alexstyl.contactstore.Contact as StoreContact
import com.swensonhe.strapikmm.model.contact.Contact as KmmContact

internal fun List<StoreContact>.toKmmContactsList(context: Context) = map { contact ->
    // Find and format the birthday
    val birthday = contact.events
        .firstOrNull { it.label == Label.DateBirthday }
        ?.value
        ?.let {
            val year = it.year ?: Calendar.getInstance().get(Calendar.YEAR)
            val month = it.month.to2DigitsFormat()
            val day = it.dayOfMonth.to2DigitsFormat()

            "$year-$month-$day"
        }

    // Map postal addresses list of json strings
    val postalAddresses = contact.postalAddresses.map {
        val address = ContactPostalAddress(
            street = it.value.street,
            poBox = it.value.poBox,
            neighborhood = it.value.neighborhood,
            city = it.value.city,
            region = it.value.region,
            postCode = it.value.postCode,
            country = it.value.country
        )

        Json.encodeToString(address)
    }

    // Map im addresses list of json strings
    val imAddresses = contact.imAddresses.map {
        val address = ContactImAddress(
            type = it.value.protocol,
            value = it.value.raw
        )

        Json.encodeToString(address)
    }

    // Map relations list of json strings
    val relations = contact.relations.map {
        val relation = ContactRelation(
            type = it.label.getLocalizedString(context.resources),
            value = it.value.name
        )

        Json.encodeToString(relation)
    }

    // Map phone numbers to list of strings
    val phoneNumbers = contact.phones.map {
        it.value.raw
    }

    // Map email addresses to list of strings
    val emailAddresses = contact.mails.map {
        it.value.raw
    }

    // Map url address to list of url strings
    val urlAddresses = contact.webAddresses.map {
        it.value.raw.toString()
    }

    // Then create the kmm contact object
    return@map KmmContact(
        firstName = contact.firstName,
        lastName = contact.lastName,
        phoneNumbers = phoneNumbers,
        emailAddresses = emailAddresses,
        middleName = contact.middleName,
        namePrefix = contact.prefix,
        nameSuffix = contact.suffix,
        birthday = birthday,
        previousFamilyName = null,
        nickname = contact.nickname,
        postalAddresses = postalAddresses,
        urlAddresses = urlAddresses,
        instantMessageAddresses = imAddresses,
        socialProfiles = imAddresses,
        relations = relations,
        type = null,
        jobTitle = contact.jobTitle,
        departmentName = null,
        organizationName = contact.organization,
        isImageDataAvailable = contact.imageData != null,
        imageData = contact.imageData?.raw,
        thumbnailImageData = contact.thumbnailUri.getBytes(context)
    )
}

fun Uri.getBytes(context: Context): ByteArray? {
    return try {
        context.contentResolver.openInputStream(this)?.use { it.buffered().readBytes() }
    } catch (e: Exception) {
        null
    }
}