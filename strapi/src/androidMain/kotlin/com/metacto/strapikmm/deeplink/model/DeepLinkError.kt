package com.metacto.strapikmm.deeplink.model

import com.appsflyer.deeplink.DeepLinkResult

actual open class DeepLinkError {
    data object Timeout : DeepLinkError()
    data object Network : DeepLinkError()
    data object HttpStatusCode : DeepLinkError()
    data object Unexpected : DeepLinkError()
    data object DeveloperError : DeepLinkError()
}


fun DeepLinkResult.Error.toError(): DeepLinkError {
    return when (this) {
        DeepLinkResult.Error.TIMEOUT -> DeepLinkError.Timeout
        DeepLinkResult.Error.NETWORK -> DeepLinkError.Network
        DeepLinkResult.Error.HTTP_STATUS_CODE -> DeepLinkError.HttpStatusCode
        DeepLinkResult.Error.UNEXPECTED -> DeepLinkError.Unexpected
        DeepLinkResult.Error.DEVELOPER_ERROR -> DeepLinkError.DeveloperError
    }
}