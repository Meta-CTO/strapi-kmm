package com.metaCTO.strapikmm.deeplink.util

import com.metaCTO.strapikmm.deeplink.model.AppAttributionResult
fun Map<*, *>.getAppAttributionResult(): AppAttributionResult {
    val isOrganic = this[AppsFlyerConstants.AF_STATUS] == AppsFlyerConstants.AF_ORGANIC_STATUS
    val extras = this.filter { it.key != AppsFlyerConstants.AF_STATUS && it.key != null } as Map<Any, Any?>

    return AppAttributionResult(
        isOrganic = isOrganic,
        extras = extras
    )
}