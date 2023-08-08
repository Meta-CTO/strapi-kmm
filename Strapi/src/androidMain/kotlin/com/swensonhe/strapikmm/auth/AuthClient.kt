package com.swensonhe.strapikmm.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.swensonhe.strapikmm.R

actual class AuthClient actual constructor(
    activity: Any?,
    private val authStateChangeListener: OnAuthStateChangeListener
) : AuthProvider {

    private lateinit var gsResult: ActivityResultLauncher<Intent>
    private lateinit var gClient: GoogleSignInClient
    private var appActivity: ComponentActivity? = null

    init {
        if (activity == null || activity !is ComponentActivity) {
            throw IllegalArgumentException("Activity must be android.app.Activity")
        } else {
            appActivity = activity
        }

        init()
    }

    actual fun init()  {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appActivity!!.getString(R.string.default_web_client_id).orEmpty())
            .requestEmail()
            .build()

        gClient = GoogleSignIn.getClient(appActivity!!, gso)
    }

    fun setActivityLauncher(launcher: ActivityResultLauncher<Intent>) {
        gsResult = launcher
    }

    fun setActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    authStateChangeListener.onAuthStateChanged(
                        dev.gitlive.firebase.auth.AuthCredential(credential)
                    )
                } catch (e: ApiException) {
                    // Handle ApiException, if necessary
                }
            }
        }
    }

    override fun signInWithApple() {
        // NOT Needed
    }

    override fun signInWithGoogle() {
        appActivity?.let {
            gsResult.launch(gClient.signInIntent)
        }
    }
}
