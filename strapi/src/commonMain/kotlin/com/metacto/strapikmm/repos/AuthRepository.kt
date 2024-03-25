package com.metacto.strapikmm.repos

import com.metacto.strapikmm.auth.AuthClient
import com.metacto.strapikmm.auth.AuthOptions
import com.metacto.strapikmm.auth.ProfileMetadata
import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.StrapiQueryBuilder
import com.metacto.strapikmm.datasource.network.services.strapi.StrapiService
import com.metacto.strapikmm.model.AuthResponse
import com.metacto.strapikmm.model.FirebaseAuthRequest
import com.metacto.strapikmm.sharedpreference.KmmPreference
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
    suspend inline fun <reified T> signInWithCurrentIdToken(
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val token = Firebase.auth.currentUser?.getIdTokenResult(forceRefresh = true)?.token
        return exchangeFirebaseToken(token.orEmpty(), null, userQueryBuilder, shouldUpdateTimeZone)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithGoogle(
        authOptions: AuthOptions?,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        authClient.setAuthOptions(authOptions)
        authClient.init()
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithGoogle({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        return signInWithCredentials(result.first, result.second, userQueryBuilder, shouldUpdateTimeZone)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithApple(
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithApple({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        return signInWithCredentials(result.first, result.second, userQueryBuilder, shouldUpdateTimeZone)
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
    suspend inline fun <reified T> signInWithEmailLink(
        emailLink: String,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        val token = Firebase.auth.signInWithEmailLink(email, emailLink).user?.getIdToken(true)
        return exchangeFirebaseToken(token.orEmpty(), null, userQueryBuilder, shouldUpdateTimeZone)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> exchangeFirebaseToken(
        idToken: String,
        profileMetadata: ProfileMetadata? = null,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val response = authService.post<AuthResponse<T>> {
            endpoint("/firebase-auth")
            authenticated(false)
            body(FirebaseAuthRequest(idToken, profileMetadata))
        }

        saveUserToken(response.jwt.orEmpty())
        val updatedUser = if (shouldUpdateTimeZone) {
            userRepository.updateTimZone(TimeZone.currentSystemDefault().id, userQueryBuilder) as T
        } else {
            userRepository.getCurrentUser(forceUpdate = true, userQueryBuilder = userQueryBuilder)
        }

        clearCachedCredentialsData()
        return updatedUser
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCredentials(
        credentials: AuthCredential,
        profileMetadata: ProfileMetadata? = null,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val token = Firebase.auth.signInWithCredential(credentials).user?.getIdToken(true)
        return exchangeFirebaseToken(token.orEmpty(), profileMetadata, userQueryBuilder, shouldUpdateTimeZone)
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
    suspend inline fun <reified T> verifyPhoneNumber(
        otp: String,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T {
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        if (verificationId.isNullOrEmpty()) throw Throwable("Unable to verify phone number")
        val credentials = PhoneAuthProvider().credential(verificationId, otp)
        return signInWithCredentials(credentials, null, userQueryBuilder, shouldUpdateTimeZone)
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