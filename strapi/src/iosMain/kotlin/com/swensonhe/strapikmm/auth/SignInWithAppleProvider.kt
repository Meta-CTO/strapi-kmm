package com.swensonhe.strapikmm.auth

import platform.AuthenticationServices.*
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.darwin.NSObject

/**
 * The [SignInWithAppleProvider] class handles Apple Sign-In authentication using the Sign In with Apple method.
 * It provides methods to initiate and handle the Apple Sign-In process.
 *
 * @param onSuccess A callback to handle successful Apple Sign-In. It receives the ID token as a [String].
 * @param onFailure A callback to handle authentication failure. It receives a [Throwable] with an error message.
 */
class SignInWithAppleProvider(
    val onSuccess: (String) -> Unit,
    val onFailure: (Throwable) -> Unit
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {

    /**
     * Initiates the Apple Sign-In process by creating a request and performing authorization requests.
     */
    fun start() {
        val request = ASAuthorizationAppleIDProvider().createRequest()
        // Specify the scopes of information to request from the user.
        request.requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)

        // Create an authorization controller with the given requests.
        val controller = ASAuthorizationController(listOf(request))
        controller.delegate = this
        controller.presentationContextProvider = this
        // Perform the request.
        controller.performRequests()
    }

    /**
     * Handles the completion of Apple Sign-In authorization and invokes the [onSuccess] or [onFailure] callbacks
     * based on the result.
     *
     * @param controller The authorization controller responsible for the completion.
     * @param didCompleteWithAuthorization The result of the authorization process.
     */
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        // Get the credential from the authorization result.
        val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        // Get the ID token from the authorization credential.
        val idToken = credential?.identityToken?.let {
            // Convert the ID token to a String. This is required because the ID token is of type `NSData`.
            return@let NSString.create(it, NSUTF8StringEncoding) as String?
        }

        idToken?.let {
            // Invoke the success callback with the ID token.
            onSuccess(idToken)
        } ?: onFailure(Throwable("idToken cannot be null")) // Invoke the failure callback.
    }

    /**
     * Handles errors that occur during the Apple Sign-In process and invokes the [onFailure] callback with an error message.
     *
     * @param controller The authorization controller responsible for the completion.
     * @param didCompleteWithError The error information associated with the failure.
     */
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        // Invoke the failure callback with the error message.
        onFailure(Throwable(didCompleteWithError.localizedDescription))
    }

    /**
     * Provides the presentation anchor for the authorization controller.
     *
     * @param controller The authorization controller that requires a presentation anchor.
     * @return An [ASPresentationAnchor] for presenting the authorization controller.
     */
    override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor? {
        return ASPresentationAnchor()
    }
}
