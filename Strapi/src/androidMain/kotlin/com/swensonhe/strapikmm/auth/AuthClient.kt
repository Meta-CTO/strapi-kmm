package com.swensonhe.strapikmm.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.swensonhe.strapikmm.R

/*
Usage: User should call Init() in composable function to initialize the auth client.
 */


actual class AuthClient actual constructor(
    activity: Any?,
    private val authStateChangeListener: OnAuthStateChangeListener
) : AuthProvider {

    private lateinit var gsResult: ManagedActivityResultLauncher<Intent, ActivityResult>
    private lateinit var gClient: GoogleSignInClient
    private var activity: Activity? = null

    init {
        if (activity == null || activity !is android.app.Activity) {
            throw IllegalArgumentException("Activity must be android.app.Activity")
        }
    }

    @Composable
    actual fun init() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity?.getString(R.string.default_web_client_id).orEmpty())
                .requestEmail()
                .build()

        gClient = GoogleSignIn.getClient(LocalContext.current, gso)
        gsResult =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = task.getResult(ApiException::class.java)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        authStateChangeListener.onAuthStateChanged(
                            dev.gitlive.firebase.auth.AuthCredential(credential)
                        )
                    }
                }
            }
    }

    override fun signInWithApple() {
        // NOT Needed
    }

    override fun signInWithGoogle() {
        activity?.let {
            gsResult.launch(gClient.signInIntent)
        }
    }
}