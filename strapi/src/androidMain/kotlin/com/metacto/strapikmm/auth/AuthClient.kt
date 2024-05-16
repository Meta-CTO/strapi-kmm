package com.metacto.strapikmm.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.metacto.strapiKMM.R
import dev.gitlive.firebase.auth.AuthCredential

actual class AuthOptions(
    val activity: Activity,
    val launcher: ActivityResultLauncher<Intent>,
    val onCanceled: () -> Unit = {}
) {
    var onResult: (ActivityResult) -> Unit = {}
}

actual class AuthClient : AuthProvider {

    private lateinit var gClient: GoogleSignInClient
    private lateinit var onResult: (AuthCredential, ProfileMetadata) -> Unit
    private lateinit var onError: (Throwable) -> Unit
    private lateinit var options: AuthOptions

    actual fun init() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(options.activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gClient = GoogleSignIn.getClient(options.activity, gso)

        options.onResult = {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                options.onCanceled.invoke()
            } else {
                setActivityResult(it)
            }
        }
    }

    private fun setActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    val profile = ProfileMetadata(
                        firstName = account.givenName,
                        lastName = account.familyName,
                        email = account.email,
                        phoneNumber = null,
                        pictureUrl = account.photoUrl?.toString()
                    )
                    onResult.invoke(AuthCredential(credential), profile)
                } catch (throwable: Throwable) {
                    onError.invoke(throwable)
                }
            }
        }
    }

    override fun signInWithApple(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        // NOT Needed
    }

    override fun signInWithGoogle(
        onSuccess: (AuthCredential, ProfileMetadata) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.onResult = onSuccess
        this.onError = onFail
        options.launcher.launch(gClient.signInIntent)
    }

    actual fun setAuthOptions(options: AuthOptions) {
        this.options = options
    }
}
