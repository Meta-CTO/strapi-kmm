package com.metacto.strapikmm.auth

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
import com.metacto.strapikmm.sharedpreference.KmmPreference
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.PhoneAuthProvider
import dev.gitlive.firebase.auth.PhoneVerificationMetadata
import dev.gitlive.firebase.auth.PhoneVerificationProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

data class AuthenticationMetadata(val idToken: String, val profileMetadata: ProfileMetadata)

interface Authenticator {
    @Throws(Throwable::class)
    suspend fun authenticateCurrentUser(): String
    @Throws(Throwable::class)
    suspend fun authenticateWithGoogle(authOptions: AuthOptions): AuthenticationMetadata
    @Throws(Throwable::class)
    suspend fun authenticateWithApple(): AuthenticationMetadata
    @Throws(Throwable::class)
    suspend fun sendEmailLink(email: String)
    @Throws(Throwable::class)
    suspend fun resendSignInLink()
    @Throws(Throwable::class)
    suspend fun verifyEmailLink(link: String): String
    @Throws(Throwable::class)
    suspend fun sendPhoneVerification(phoneNumber: String, phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata
    @Throws(Throwable::class)
    suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata
    @Throws(Throwable::class)
    suspend fun verifyPhoneVerification(code: String): String
    @Throws(Throwable::class)
    suspend fun linkPhoneNumber(code: String)
}

class FirebaseAuthenticator(
    private val actionCodeSettings: ActionCodeSettings,
    private val sharedPreference: KmmPreference
): Authenticator {
    private val authClient by lazy { AuthClient() }

    @Throws(Throwable::class)
    override suspend fun authenticateCurrentUser(): String {
        val user = Firebase.auth.currentUser ?: throw NetworkErrorMapper.mapToAppException(
            "Unable to authenticate current user, current user is null",
            -1
        )
        return user.getIdToken(true) ?: throw NetworkErrorMapper.mapToAppException(
            "Unable to getIdToken",
            -1
        )
    }

    @Throws(Throwable::class)
    override suspend fun authenticateWithGoogle(authOptions: AuthOptions): AuthenticationMetadata {
        authClient.setAuthOptions(authOptions)
        authClient.init()
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithGoogle({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        val idToken = authenticateWithCredentials(result.first)
        return AuthenticationMetadata(idToken, result.second)
    }

    @Throws(Throwable::class)
    override suspend fun authenticateWithApple(): AuthenticationMetadata {
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithApple({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }

        val idToken = authenticateWithCredentials(result.first)
        return AuthenticationMetadata(idToken, result.second)
    }

    @Throws(Throwable::class)
    override suspend fun sendEmailLink(email: String) {
        sharedPreference.putSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL, email)
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }

    @Throws(Throwable::class)
    override suspend fun resendSignInLink() {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw NetworkErrorMapper.mapToAppException(
            "Email is null or empty, please try again.",
            -1
        )
        sendEmailLink(email)
    }

    @Throws(Throwable::class)
    override suspend fun verifyEmailLink(link: String): String {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw NetworkErrorMapper.mapToAppException(
            "Email is null or empty, please try again.",
            -1
        )
        val user = Firebase.auth.signInWithEmailLink(email, link).user ?: throw NetworkErrorMapper.mapToAppException(
            "Unable to sign in with email link",
            -1
        )
        return user.getIdToken(true) ?: throw NetworkErrorMapper.mapToAppException(
            "Unable to getIdToken",
            -1
        )
    }

    @Throws(Throwable::class)
    override suspend fun sendPhoneVerification(phoneNumber: String, phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata {
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
    override suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata {
        val phoneNumber = sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER)
        if (phoneNumber.isNullOrEmpty()) throw NetworkErrorMapper.mapToAppException(
            "Invalid phone number",
            -1
        )
        return PhoneAuthProvider().verifyPhoneNumber(phoneNumber, phoneVerificationProvider)
    }

    @Throws(Throwable::class)
    override suspend fun verifyPhoneVerification(code: String): String {
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        if (verificationId.isNullOrEmpty()) throw NetworkErrorMapper.mapToAppException(
            "Unable to verify phone number",
            -1
        )
        val credentials = PhoneAuthProvider().credential(verificationId, code)
        return authenticateWithCredentials(credentials)
    }

    @Throws(Throwable::class)
    override suspend fun linkPhoneNumber(code: String) {
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        if (verificationId.isNullOrEmpty()) throw NetworkErrorMapper.mapToAppException(
            "Unable to verify phone number",
            -1
        )
        val credentials = PhoneAuthProvider().credential(verificationId, code)
        if (Firebase.auth.currentUser == null) throw NetworkErrorMapper.mapToAppException(
            "Firebase user is null, unable to link phone number",
            -1
        )
        Firebase.auth.currentUser?.linkWithCredential(credentials)
    }

    @Throws(Throwable::class)
    private suspend fun authenticateWithCredentials(credentials: AuthCredential): String {
        val user = Firebase.auth.signInWithCredential(credentials).user ?: throw NetworkErrorMapper.mapToAppException(
            "Signing in failed user is null",
            -1
        )
        return user.getIdToken(true) ?: throw NetworkErrorMapper.mapToAppException(
            "Unable to getIdToken",
            -1
        )
    }
}