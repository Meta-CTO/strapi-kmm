package com.metacto.strapikmm.repos

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.sharedpreference.KmmPreference
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class LogoutUseCase(
    private val sharedPreference: KmmPreference
) {
    suspend fun logout() = executeCatching {
        sharedPreference.clearValue(SharedConstants.CACHED_USER_DATA)
        sharedPreference.clearSecureValue(SharedConstants.ACCESS_TOKEN)
        sharedPreference.putBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
        Firebase.auth.signOut()
    }
}