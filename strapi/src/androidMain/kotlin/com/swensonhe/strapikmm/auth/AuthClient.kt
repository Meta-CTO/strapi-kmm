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
import com.swensonhe.strapiKMM.R
import dev.gitlive.firebase.auth.AuthCredential

/**
 * The [AuthOptions] class provides the configuration options for the [AuthClient].
 *
 * @param activity The Android [Activity].
 * @param launcher The activity result launcher [ActivityResultLauncher] for the [AuthClient].
 */
actual class AuthOptions(
    val activity: Activity,
    val launcher: ActivityResultLauncher<Intent>,
    val onCancelled: () -> Unit = {}
) {
    /**
     * A lambda function to handle the result of the authentication process.
     */
    var onResult: (ActivityResult) -> Unit = {}
}

/**
 * The [AuthClient] class is responsible for authenticating users using various providers
 * such as Google Sign-In (For Android).
 */
actual class AuthClient  : AuthProvider {

    // The Google Sign-In client
    private lateinit var gClient: GoogleSignInClient
    // The result handler for the Google Sign-In client
    private lateinit var onResult: (AuthCredential) -> Unit
    // the error handler for the Google Sign-In client
    private lateinit var onError: (Throwable) -> Unit
    // The authentication options
    private lateinit var options: AuthOptions

    /**
     * Initializes the [AuthClient] with the provided [AuthOptions].
     *
     * @param options The [AuthOptions] to use for configuration.
     */
    actual fun init() {
        // Get the Google Sign-In options for the client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(options.activity.getString(R.string.default_web_client_id)) // Set the web client ID from the generated google-services.json file
            .requestEmail() // Request the user's email address
            .requestProfile() // Request the user's profile information
            .build() // Build the Google Sign-In options

        // Create the Google Sign-In client
        gClient = GoogleSignIn.getClient(options.activity, gso)

        // Set the result handler for the Google Sign-In client
        options.onResult = {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                // Invoke the onCanceled handler
                options.onCancelled.invoke()
            } else {
                // Handle the result
                setActivityResult(it)
            }
        }
    }

    /**
     * Handles the result of the Google Sign-In client.
     *
     * @param result The [ActivityResult] from the Google Sign-In client.
     */
    private fun setActivityResult(result: ActivityResult) {
        // Check if the result is OK
        if (result.resultCode == Activity.RESULT_OK) {
            // Check if the result data is not null
            if (result.data != null) {
                // Get the signed in account from the result data
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Get the account from the task result
                    val account = task.getResult(ApiException::class.java)
                    // Get the credential from the account ID token
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    // Invoke the result handler with the credential
                    onResult.invoke(AuthCredential(credential))
                } catch (throwable: Throwable) {
                    // Invoke the error handler with the throwable
                    onError.invoke(throwable)
                }
            }
        }
    }

    override fun signInWithApple(onSuccess: (AuthCredential) -> Unit, onFail: (Throwable) -> Unit) {
        throw IllegalAccessException("Apple Sign-In is not supported on Android")
    }

    /**
     * Signs in the user using Google Sign-In.
     *
     * @param onSuccess The lambda function to invoke when the user is successfully signed in.
     * @param onFail The lambda function to invoke when the user sign in fails.
     */
    override fun signInWithGoogle(
        onSuccess: (AuthCredential) -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        // Set the result handler
        this.onResult = onSuccess
        // Set the error handler
        this.onError = onFail
        // Launch the Google Sign-In intent using the launcher
        options.launcher.launch(gClient.signInIntent)
    }

    /**
     * Sets the authentication options for this [AuthClient].
     *
     * @param options The authentication options.
     * @throws IllegalArgumentException if the provided options are null.
     */
    actual fun setAuthOptions(options: AuthOptions?) {
        if (options == null) throw IllegalArgumentException("options cannot be null")
        this.options = options
    }
}
