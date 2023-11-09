package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.auth.AuthClient
import com.swensonhe.strapikmm.auth.AuthOptions
import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.AuthResponse
import com.swensonhe.strapikmm.model.FirebaseAuthRequest
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.PhoneAuthProvider
import dev.gitlive.firebase.auth.PhoneVerificationMetadata
import dev.gitlive.firebase.auth.PhoneVerificationProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.TimeZone
import kotlin.coroutines.resumeWithException

/**
 * Repository for authentication-related operations.
 *
 * @property authService The service for authentication-related API calls.
 * @property userRepository The repository for user-related operations.
 * @property sharedPreference The shared preference used for storing user-related data.
 * @property actionCodeSettings The settings for handling authentication actions.
 */
class AuthRepository(
    val authService: StrapiService,
    val userRepository: UserRepository,
    val sharedPreference: KmmPreference,
    private val actionCodeSettings: ActionCodeSettings
) {

    // Lazy initialization of the authentication client.
    val authClient by lazy { AuthClient() }

    /**
     * Sign in with the current user's ID token.
     *
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the sign-in operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCurrentIdToken(): T {
        // Force refresh the current user's ID token to ensure it is up-to-date.
        val token = Firebase.auth.currentUser?.getIdTokenResult(forceRefresh = true)?.token
        // Exchange the ID token for a JWT.
        return exchangeFirebaseToken(token.orEmpty())
    }

    /**
     * Sign in with Google authentication.
     *
     * @param authOptions Optional authentication options.
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the sign-in operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithGoogle(authOptions: AuthOptions?): T {
        // Set authentication options.
        authClient.setAuthOptions(authOptions)
        // Initialize the authentication client.
        authClient.init()
        // Sign in with Google.
        val credentials = suspendCancellableCoroutine { cont ->
            authClient.signInWithGoogle({
                cont.resumeWith(Result.success(it))
            }, {
                cont.resumeWithException(it)
            })
        }

        // Exchange the ID token for a JWT using the credentials.
        return signInWithCredentials(credentials)
    }

    /**
     * Sign in with Apple authentication.
     *
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the sign-in operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithApple(): T {
        // Sign in with Apple.
        val credentials = suspendCancellableCoroutine { cont ->
            authClient.signInWithApple({
                cont.resumeWith(Result.success(it))
            }, {
                cont.resumeWithException(it)
            })
        }

        // Exchange the ID token for a JWT using the credentials.
        return signInWithCredentials(credentials)
    }

    /**
     * Send a sign-in link to the provided email address.
     *
     * @param email The email address to send the sign-in link to.
     * @throws Throwable in case of exceptions during the operation.
     */
    @Throws(Throwable::class)
    suspend fun sendSignInLinkToEmail(email: String) {
        // Save the email address to the shared preference.
        sharedPreference.putSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL, email)
        // Send the sign-in link to the provided email address.
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }

    /**
     * Resend the sign-in link to the email address saved in the shared preference.
     *
     * @throws Throwable in case of exceptions during the operation.
     */
    @Throws(Throwable::class)
    suspend fun resendSignInLink() {
        // Retrieve the email address from the shared preference.
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        // Check if the email address is null or empty and throw an exception if so.
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        // Resend the sign-in link to the email address.
        sendSignInLinkToEmail(email)
    }

    /**
     * Sign in with the provided email link.
     *
     * @param emailLink The email link to sign in with.
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the sign-in operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithEmailLink(emailLink: String): T {
        // Retrieve the email address from the shared preference.
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        // Check if the email address is null or empty and throw an exception if so.
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        // Sign in with the provided email link and email address and get the firebase id token.
        val token = Firebase.auth.signInWithEmailLink(email, emailLink).user?.getIdToken(true)
        // Exchange the ID token for a JWT.
        return exchangeFirebaseToken(token.orEmpty())
    }

    /**
     * Exchange a Firebase ID token for a JWT.
     *
     * @param idToken The Firebase ID token to exchange for a JWT.
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the exchange operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> exchangeFirebaseToken(idToken: String): T {
        // Exchange the Firebase ID token for a JWT.
        val response = authService.post<AuthResponse<T>> {
            // Set the endpoint.
            endpoint("/firebase-auth")
            // remove any authentication headers if present
            authenticated(false)
            // Set the request body.
            body(FirebaseAuthRequest(idToken))
        }

        // Save the JWT to the shared preference.
        saveUserToken(response.jwt.orEmpty())
        // Update the user's timezone.
        val updatedUser = userRepository.updateTimZone(TimeZone.currentSystemDefault().id) as T

        // Clear cached credentials data from the shared preference if exists.
        clearCachedCredentialsData()
        // Return the updated user.
        return updatedUser
    }

    /**
     * Sign in with the provided credentials.
     *
     * @param credentials The credentials to sign in with.
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the sign-in operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCredentials(credentials: AuthCredential): T {
        // Sign in with the provided credentials and get the firebase id token.
        val token = Firebase.auth.signInWithCredential(credentials).user?.getIdToken(true)
        // Exchange the ID token for a JWT.
        return exchangeFirebaseToken(token.orEmpty())
    }

    /**
     * Send a phone verification code to the provided phone number.
     *
     * @param phoneNumber The phone number to send the verification code to.
     * @param phoneVerificationProvider The provider to use for sending the verification code.
     * @throws Throwable in case of exceptions during the operation.
     * @return The metadata of the phone verification operation.
     */
    @Throws(Throwable::class)
    suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        phoneVerificationProvider: PhoneVerificationProvider
    ): PhoneVerificationMetadata {
        // Send the phone verification code to the provided phone number.
        val metadata = PhoneAuthProvider().verifyPhoneNumber(phoneNumber, phoneVerificationProvider)
        // Save the verification ID to the shared preference.
        sharedPreference.putSecureString(
            SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID,
            metadata.verificationId
        )
        // Save the phone number to the shared preference.
        sharedPreference.putSecureString(
            SharedConstants.VERIFICATION_PHONE_NUMBER,
            metadata.phoneNumber
        )
        // Return the metadata of the phone verification operation.
        return metadata
    }

    /**
     * Resend the phone verification code to the phone number saved in the shared preference.
     *
     * @param phoneVerificationProvider The provider to use for sending the verification code.
     * @throws Throwable in case of exceptions during the operation.
     * @return The metadata of the phone verification operation.
     */
    @Throws(Throwable::class)
    suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata {
        // Retrieve the phone number from the shared preference.
        val phoneNumber =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER)
        // Check if the phone number is null or empty and throw an exception if so.
        if (phoneNumber.isNullOrEmpty()) throw Throwable("Invalid phone number")
        // Resend the phone verification code to the phone number.
        return sendPhoneVerificationCode(phoneNumber, phoneVerificationProvider)
    }

    /**
     * Verify the phone number with the provided OTP.
     *
     * @param otp The OTP to verify the phone number with.
     * @throws Throwable in case of exceptions during the operation.
     * @return The result of the verification operation.
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> verifyPhoneNumber(otp: String): T {
        // Retrieve the verification ID from the shared preference.
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        // Check if the verification ID is null or empty and throw an exception if so.
        if (verificationId.isNullOrEmpty()) throw Throwable("Unable to verify phone number")
        // Verify the phone number with the provided OTP and get the firebase id token.
        val credentials = PhoneAuthProvider().credential(verificationId, otp)
        // Exchange the credentials for a JWT.
        return signInWithCredentials(credentials)
    }

    /**
     * Save the JWT to the shared preference.
     *
     * @param token The JWT to save.
     */
    fun saveUserToken(token: String) {
        sharedPreference.putSecureString(SharedConstants.ACCESS_TOKEN, token)
    }

    /**
     * Check if the user is logged in.
     *
     * @return True if the user is logged in, false otherwise.
     */
    fun isUserLoggedIn(): Boolean {
        // Check if the JWT is not null or empty.
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    /**
     * Sign out the current user and clear the cached user data.
     * Sign out the current user from Firebase.
     */
    suspend fun signOut() {
        sharedPreference.clearValue(SharedConstants.CACHED_USER_DATA)
        sharedPreference.clearSecureValue(SharedConstants.ACCESS_TOKEN)
        Firebase.auth.signOut()
    }

    /**
     * Clear cached credentials data from the shared preference.
     */
    fun clearCachedCredentialsData() {
        // Clear cached email link data.
        sharedPreference.clearSecureValue(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        // Clear cached phone verification data.
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER)
        // Clear cached phone verification ID.
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
    }
}