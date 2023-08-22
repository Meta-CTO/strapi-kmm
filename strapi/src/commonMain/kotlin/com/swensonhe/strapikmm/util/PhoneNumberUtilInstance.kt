package com.swensonhe.strapikmm.util

import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
import io.michaelrocks.libphonenumber.kotlin.Phonenumber

internal expect class PhoneNumberUtilInstance(context: Any?) {
    val util: PhoneNumberUtil
}

class PhoneFormatter(context: Any? = null) {
    private val phoneNumberUtilInstance = PhoneNumberUtilInstance(context)
    private val phoneNumberUtil = phoneNumberUtilInstance.util

    fun getPhoneNumberUtil(): PhoneNumberUtil {
        return phoneNumberUtil
    }

    fun formatPhoneNumber(phoneNumber: Phonenumber.PhoneNumber): String {
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
    }

    fun formatPhoneNumber(phoneNumber: String, defaultRegion: String = "US"): String {
        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
        return phoneNumberUtil.format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
    }

    fun isValidPhoneNumber(phoneNumber: String, defaultRegion: String = "US"): Boolean {
        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
        return phoneNumberUtil.isValidNumber(parsedPhoneNumber)
    }

    fun getCountryCode(phoneNumber: String, defaultRegion: String = "US"): Int {
        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
        return parsedPhoneNumber.countryCode
    }

    fun getNationalNumber(phoneNumber: String, defaultRegion: String = "US"): Long {
        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
        return parsedPhoneNumber.nationalNumber
    }

    fun parsePhoneNumber(phoneNumber: String, defaultRegion: String = "US"): Phonenumber.PhoneNumber {
        return phoneNumberUtil.parse(phoneNumber, defaultRegion)
    }

    fun formatNationalNumber(phoneNumber: Phonenumber.PhoneNumber): String {
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
    }

    fun formatInternationalNumber(phoneNumber: Phonenumber.PhoneNumber): String {
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    fun formatE164Number(phoneNumber: Phonenumber.PhoneNumber): String {
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
    }

    fun formatRFC3966Number(phoneNumber: Phonenumber.PhoneNumber): String {
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966)
    }
}