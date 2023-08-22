package com.swensonhe.strapikmm.util

import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
import io.michaelrocks.libphonenumber.kotlin.metadata.init.MokoAssetResourceMetadataLoader

internal actual class PhoneNumberUtilInstance actual constructor(context: Any?) {
    actual val util: PhoneNumberUtil = PhoneNumberUtil.createInstance(
        MokoAssetResourceMetadataLoader()
    )
}