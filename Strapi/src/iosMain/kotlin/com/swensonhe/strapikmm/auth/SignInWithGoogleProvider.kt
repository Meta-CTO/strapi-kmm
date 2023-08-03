package com.swensonhe.strapikmm.auth

import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn

import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

class SignInWithGoogleProvider(val onSuccess: (String) -> Unit, val onFailure: (Throwable) -> Unit) {
    fun start() {
        FIRApp.defaultApp()?.options?.clientID?.let {clientID ->
            GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID)
            val windowScene = UIApplication.sharedApplication.connectedScenes().firstOrNull() as? UIWindowScene
            val window = windowScene?.windows?.firstOrNull() as? UIWindow
            val rootViewController = window?.rootViewController

            rootViewController?.let { presentingViewController ->
                GIDSignIn.sharedInstance.signInWithPresentingViewController(presentingViewController, completion = { result, error ->
                    error?.let {
                        onFailure(Throwable(it.localizedDescription))
                    }

                    result?.user?.idToken?.tokenString?.let { idToken ->
                        onSuccess(idToken)
                    }
                })
            }
        }
    }
}