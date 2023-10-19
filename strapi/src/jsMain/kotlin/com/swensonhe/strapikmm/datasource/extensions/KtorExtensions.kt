package com.swensonhe.strapikmm.datasource.extensions

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.sharedpreference.TokenHandler
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.plugins.DefaultRequest

fun DefaultRequest.DefaultRequestBuilder.handleJsAuthenticationHeader(preference: KmmPreference) {
    val sharedToken = preference.getSecureString(SharedConstants.ACCESS_TOKEN)
    val token = TokenHandler.token

    val finalToken = if (sharedToken.isNullOrEmpty().not()) {
        sharedToken
    } else if (token.isNotEmpty()) {
        token
    } else {
        null
    }

    if(strapiNetworkLogLevel != NetworkLogLevel.NONE) {
        console.log("finalToken: $finalToken")
    }

    if (finalToken.isNullOrEmpty().not()) {
        headers.append(
            SharedConstants.AUTHORIZATION_HEADER,
            "${SharedConstants.BEARER} $finalToken"
        )
    }
    headers.remove(KmmBaseService.IS_AUTHENTICATED)
}