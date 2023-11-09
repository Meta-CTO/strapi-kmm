package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn

import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

/**
 * The [SignInWithGoogleProvider] class handles Google Sign-In authentication using Google Sign-In framework.
 * It provides methods to initiate and handle the Google Sign-In process.
 *
 * @param onSuccess A callback to handle successful Google Sign-In. It receives the ID token as a [String].
 * @param onFailure A callback to handle authentication failure. It receives a [Throwable] with an error message.
 */
class SignInWithGoogleProvider(
    val onSuccess: (String) -> Unit,
    val onFailure: (Throwable) -> Unit
) {
    /**
     * Initiates the Google Sign-In process by configuring Google Sign-In and presenting the authentication UI.
     *
     * @throws [Throwable] if the client ID or presenting view controller is not available.
     */
    @Throws(Throwable::class)
    fun start() {
        // Get the client ID from the Firebase app options.
        val clientId =
            FIRApp.defaultApp()?.options?.clientID ?: throw Throwable("clientId cannot be null")
        // Get the presenting view controller from the window scene.
        val windowScene =
            UIApplication.sharedApplication.connectedScenes().firstOrNull() as? UIWindowScene
        // Get the window from the window scene.
        val window = windowScene?.windows?.firstOrNull() as? UIWindow
        // Get the root view controller from the window.
        val presentingViewController =
            window?.rootViewController ?: throw Throwable("presentingViewController cannot be null")

        // Configure Google Sign-In with the client ID.
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientId)

        // Present the Google Sign-In authentication UI.
        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            presentingViewController,
            completion = { result, error ->
                error?.let {
                    // Invoke the failure callback.
                    onFailure(Throwable(it.localizedDescription))
                }

                // Invoke the success callback with the ID token.
                result?.user?.idToken?.tokenString?.let { idToken ->
                    onSuccess(idToken)
                }
            }
        )
    }
}