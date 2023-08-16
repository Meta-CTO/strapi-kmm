package com.swensonhe.strapikmm.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.swensonhe.strapikmm.R
import dev.gitlive.firebase.auth.AuthCredential

actual class AuthOptions(
    val activity: Activity,
    val launcher: ActivityResultLauncher<Intent>
) {
    var onResult: (ActivityResult) -> Unit = {}
}

actual class AuthClient actual constructor(
    private val options: AuthOptions?
) : AuthProvider {

    private lateinit var gClient: GoogleSignInClient
    private lateinit var onResult: (AuthCredential) -> Unit
    private lateinit var onError: (Throwable) -> Unit

    init {
        require(options != null) {
            "AuthMetaData must be initialized"
        }
        init()
    }

    actual fun init() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(options!!.activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gClient = GoogleSignIn.getClient(options.activity, gso)

        options.onResult = {
            setActivityResult(it)
        }
    }

    private fun setActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    onResult.invoke(AuthCredential(credential))
                } catch (throwable: Throwable) {
                    onError.invoke(throwable)
                }
            }
        }
    }

    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        // NOT Needed
    }

    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.onResult = onSuccess
        this.onError = onFail
        options!!.launcher.launch(gClient.signInIntent)
    }
}
