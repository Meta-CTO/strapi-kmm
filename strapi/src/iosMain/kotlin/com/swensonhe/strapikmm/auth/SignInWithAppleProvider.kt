package com.swensonhe.strapikmm.auth

import platform.AuthenticationServices.*
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.darwin.NSObject

import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class SignInWithAppleProvider(
    val onSuccess: (String, ProfileMetadata) -> Unit,
    val onFailure: (Throwable) -> Unit
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {
    fun start() {
        val request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)

        val controller = ASAuthorizationController(listOf(request))
        controller.delegate = this
        controller.presentationContextProvider = this
        controller.performRequests()
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        val credential =
            didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        val idToken = credential?.identityToken?.let {
            return@let NSString.create(it, NSUTF8StringEncoding) as String?
        }

        val profile = ProfileMetadata(
            firstName = credential?.fullName?.givenName,
            lastName = credential?.fullName?.familyName,
            email = credential?.email,
            phoneNumber = null,
            pictureUrl = null
        )

        idToken?.let {
            onSuccess(idToken, profile)
        } ?: onFailure(Throwable("idToken cannot be null"))
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        onFailure(Throwable(didCompleteWithError.localizedDescription))
    }

    override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor {
        return ASPresentationAnchor()
    }
}