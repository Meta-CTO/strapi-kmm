package com.metacto.strapikmm.auth

import com.metacto.strapikmm.constants.SharedConstants
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

interface IAuthenticator {
    @Throws(Throwable::class)
    suspend fun authenticateCurrentUser(): String?
    @Throws(Throwable::class)
    suspend fun authenticateWithCredentials(credentials: AuthCredential): String?
    @Throws(Throwable::class)
    suspend fun authenticateWithGoogle(authOptions: AuthOptions?): Pair<String?, ProfileMetadata>
    @Throws(Throwable::class)
    suspend fun authenticateWithApple(): Pair<String?, ProfileMetadata>
    @Throws(Throwable::class)
    suspend fun sendEmailLink(email: String)
    @Throws(Throwable::class)
    suspend fun resendSignInLink()
    @Throws(Throwable::class)
    suspend fun verifyEmailLink(link: String): String?
    @Throws(Throwable::class)
    suspend fun sendPhoneVerification(phoneNumber: String, phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata
    @Throws(Throwable::class)
    suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata
    @Throws(Throwable::class)
    suspend fun verifyPhoneVerification(code: String): String?
}

class Authenticator(
    private val actionCodeSettings: ActionCodeSettings,
    private val sharedPreference: KmmPreference
): IAuthenticator {
    private val authClient by lazy { AuthClient() }
    override suspend fun authenticateCurrentUser(): String? {
        return Firebase.auth.currentUser?.getIdToken(true)
    }

    override suspend fun authenticateWithGoogle(authOptions: AuthOptions?): Pair<String?, ProfileMetadata> {
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
        return Pair(idToken, result.second)
    }

    override suspend fun authenticateWithApple(): Pair<String?, ProfileMetadata> {
        val result = suspendCancellableCoroutine { cont ->
            authClient.signInWithApple({ credentials, profileMetadata ->
                cont.resumeWith(Result.success(Pair(credentials, profileMetadata)))
            }, {
                cont.resumeWithException(it)
            })
        }
        val idToken = authenticateWithCredentials(result.first)
        return Pair(idToken, result.second)
    }

    override suspend fun sendEmailLink(email: String) {
        sharedPreference.putSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL, email)
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }
    override suspend fun resendSignInLink() {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        sendEmailLink(email)
    }

    override suspend fun verifyEmailLink(link: String): String? {
        val email = sharedPreference.getSecureString(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        if (email.isNullOrEmpty()) throw Throwable("Email is null or empty, please try again.")
        return Firebase.auth.signInWithEmailLink(email, link).user?.getIdToken(true)
    }

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

    override suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata {
        val phoneNumber = sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER)
        if (phoneNumber.isNullOrEmpty()) throw Throwable("Invalid phone number")
        return PhoneAuthProvider().verifyPhoneNumber(phoneNumber, phoneVerificationProvider)
    }

    override suspend fun verifyPhoneVerification(code: String): String? {
        val verificationId =
            sharedPreference.getSecureString(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
        if (verificationId.isNullOrEmpty()) throw Throwable("Unable to verify phone number")
        val credentials = PhoneAuthProvider().credential(verificationId, code)
        return authenticateWithCredentials(credentials)
    }


    override suspend fun authenticateWithCredentials(credentials: AuthCredential): String? {
        return Firebase.auth.signInWithCredential(credentials).user?.getIdToken(true)
    }
}