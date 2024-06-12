package com.metacto.strapikmm.auth

import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper

import platform.UIKit.UIViewController

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class SignInWithGoogleProvider(
    private val presentingViewController: UIViewController,
    val onSuccess: (String, ProfileMetadata) -> Unit,
    val onFailure: (Throwable) -> Unit
) {
    @Throws(Throwable::class)
    fun start() {
        val clientId = FIRApp.defaultApp()?.options?.clientID
            ?: throw NetworkErrorMapper.mapToAppException(
                "clientId cannot be null",
                -1
            )

        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientId)

        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            presentingViewController,
            completion = { result, error ->
                error?.let {
                    onFailure(NetworkErrorMapper.mapThrowable(it))
                }

                result?.user?.idToken?.tokenString?.let { idToken ->
                    val profile = ProfileMetadata(
                        firstName = result.user.profile?.givenName,
                        lastName = result.user.profile?.familyName,
                        email = result.user.profile?.email,
                        phoneNumber = null,
                        pictureUrl = result.user.profile?.imageURLWithDimension(1080u)?.absoluteString
                    )

                    onSuccess(idToken, profile)
                }
            }
        )
    }
}