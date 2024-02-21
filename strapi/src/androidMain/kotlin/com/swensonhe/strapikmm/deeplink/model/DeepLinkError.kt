package com.swensonhe.strapikmm.deeplink.model

import com.appsflyer.deeplink.DeepLinkResult
import com.swensonhe.andrew_kmm.utilities.CommonParcelable
import com.swensonhe.andrew_kmm.utilities.CommonParcelize

@CommonParcelize
actual open class DeepLinkError: CommonParcelable {
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