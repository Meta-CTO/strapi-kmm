package com.metaCTO.strapikmm.repos

import com.metaCTO.strapikmm.auth.AuthClient
import com.metaCTO.strapikmm.auth.AuthOptions
import com.metaCTO.strapikmm.auth.ProfileMetadata
import com.metaCTO.strapikmm.constants.SharedConstants
import com.metaCTO.strapikmm.datasource.network.services.strapi.StrapiService
import com.metaCTO.strapikmm.model.AuthResponse
import com.metaCTO.strapikmm.model.FirebaseAuthRequest
import com.metaCTO.strapikmm.sharedpreference.KmmPreference
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

class AuthRepository(
    val authService: StrapiService,
    val userRepository: UserRepository,
    val sharedPreference: KmmPreference,
    private val actionCodeSettings: ActionCodeSettings
) {

    val authClient by lazy { AuthClient() }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCurrentIdToken(): T {
        val token = Firebase.auth.currentUser?.getIdTokenResult(forceRefresh = true)?.token
        return exchangeFirebaseToken(token.orEmpty())
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithGoogle(authOptions: AuthOptions?): T {
        authClient.setAuthOptions(authOptions)
        authClient.init()
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithGoogle({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        return signInWithCredentials(result.first, result.second)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithApple(): T {
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithApple({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        return signInWithCredentials(result.first, result.second)
    }

    @Throws(Throwable::class)
    suspend fun sendSignInLinkToEmail(email: String) {
        sharedPreference.putSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL, email)
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }

    @Throws(Throwable::class)
    suspend fun resendSignInLink() {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        sendSignInLinkToEmail(email)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithEmailLink(emailLink: String): T {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        val token = Firebase.auth.signInWithEmailLink(email, emailLink).user?.getIdToken(true)
        return exchangeFirebaseToken(token.orEmpty())
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> exchangeFirebaseToken(
        idToken: String,
        profileMetadata: ProfileMetadata? = null
    ): T {
        val response = authService.post<AuthResponse<T>> {
            endpoint("/firebase-auth")
            authenticated(false)
            body(FirebaseAuthRequest(idToken, profileMetadata))
        }

        saveUserToken(response.jwt.orEmpty())
        val updatedUser = userRepository.updateTimZone(TimeZone.currentSystemDefault().id) as T

        clearCachedCredentialsData()
        return updatedUser
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCredentials(
        credentials: AuthCredential,
        profileMetadata: ProfileMetadata? = null
    ): T {
        val token = Firebase.auth.signInWithCredential(credentials).user?.getIdToken(true)
        return exchangeFirebaseToken(token.orEmpty(), profileMetadata)
    }

    @Throws(Throwable::class)
    suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        phoneVerificationProvider: PhoneVerificationProvider
    ): PhoneVerificationMetadata {
        val metadata = PhoneAuthProvider().verifyPhoneNumber(phoneNumber, phoneVerificationProvider)
        sharedPreference.putSecureString(
            SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID,
            metadata.verificationId
        )
        sharedPreference.putSecureString(
            SharedConstants.VERIFICATION_PHONE_NUMBER,
            metadata.phoneNumber
        )
        return metadata
    }

    @Throws(Throwable::class)
    suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata {
        val phoneNumber =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER)
        if (phoneNumber.isNullOrEmpty()) throw Throwable("Invalid phone number")
        return sendPhoneVerificationCode(phoneNumber, phoneVerificationProvider)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> verifyPhoneNumber(otp: String): T {
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        if (verificationId.isNullOrEmpty()) throw Throwable("Unable to verify phone number")
        val credentials = PhoneAuthProvider().credential(verificationId, otp)
        return signInWithCredentials(credentials)
    }

    fun saveUserToken(token: String) {
        sharedPreference.putSecureString(SharedConstants.ACCESS_TOKEN, token)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    suspend fun signOut() {
        sharedPreference.clearValue(SharedConstants.CACHED_USER_DATA)
        sharedPreference.clearSecureValue(SharedConstants.ACCESS_TOKEN)
        Firebase.auth.signOut()
    }

    fun clearCachedCredentialsData() {
        sharedPreference.clearSecureValue(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER)
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
    }
}