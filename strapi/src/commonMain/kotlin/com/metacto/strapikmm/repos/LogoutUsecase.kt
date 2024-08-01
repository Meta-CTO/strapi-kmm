package com.metacto.strapikmm.repos

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.sharedpreference.KmmPreference

class LogoutUseCase(
    private val sharedPreference: KmmPreference
) {
    suspend fun logout() = executeCatching {
        sharedPreference.clearValue(SharedConstants.CACHED_USER_DATA)
        sharedPreference.clearSecureValue(SharedConstants.ACCESS_TOKEN)
        sharedPreference.putBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
    }
}