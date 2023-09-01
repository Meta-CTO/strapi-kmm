package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn

import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

class SignInWithGoogleProvider(
    val onSuccess: (String) -> Unit,
    val onFailure: (Throwable) -> Unit
) {
    @Throws(Throwable::class)
    fun start() {
        val clientId =
            FIRApp.defaultApp()?.options?.clientID ?: throw Throwable("Client Id cannot be null")
        val windowScene =
            UIApplication.sharedApplication.connectedScenes().firstOrNull() as? UIWindowScene
        val window = windowScene?.windows?.firstOrNull() as? UIWindow
        val presentingViewController =
            window?.rootViewController ?: throw Throwable("Cannot find presentingViewController")

        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientId)

        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            presentingViewController,
            completion = { result, error ->
                error?.let {
                    onFailure(Throwable(it.localizedDescription))
                }

                result?.user?.idToken?.tokenString?.let { idToken ->
                    onSuccess(idToken)
                }
            }
        )
    }
}