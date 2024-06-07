package com.metacto.strapikmm.repos

import com.metacto.strapikmm.auth.AuthOptions
import com.metacto.strapikmm.auth.Authenticator
import com.metacto.strapikmm.auth.ProfileMetadata
import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.StrapiQueryBuilder
import com.metacto.strapikmm.datasource.network.services.strapi.StrapiService
import com.metacto.strapikmm.errorhandling.isValidJson
import com.metacto.strapikmm.errorhandling.mapError
import com.metacto.strapikmm.model.AuthResponse
import com.metacto.strapikmm.model.FirebaseAuthRequest
import com.metacto.strapikmm.model.OverrideUserRequest
import com.metacto.strapikmm.sharedpreference.KmmPreference
import dev.gitlive.firebase.auth.PhoneVerificationMetadata
import dev.gitlive.firebase.auth.PhoneVerificationProvider
import kotlinx.datetime.TimeZone

inline fun <reified T> executeCatching(block: () -> T): T {
//    return try {
//        block()
//    } catch (throwable: Throwable) {
//        val errorMessage = throwable.message.orEmpty()
//        if (isValidJson(errorMessage)) {
//            throw throwable
//        } else {
//            throw Throwable(errorMessage.mapError(-1))
//        }
//    }

    return block.invoke()
}

class AuthRepository(
    val authService: StrapiService,
    val userRepository: UserRepository,
    private val logoutUseCase: LogoutUseCase,
    val sharedPreference: KmmPreference,
    val authenticator: Authenticator
) {
    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithCurrentIdToken(
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T = executeCatching {
        val token = authenticator.authenticateCurrentUser()
        return exchangeFirebaseToken(token, null, userQueryBuilder, shouldUpdateTimeZone)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithGoogle(
        authOptions: AuthOptions,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T  = executeCatching {
        val authenticationMetadata = authenticator.authenticateWithGoogle(authOptions)

        return exchangeFirebaseToken(
            authenticationMetadata.idToken,
            authenticationMetadata.profileMetadata,
            userQueryBuilder,
            shouldUpdateTimeZone
        )
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithApple(
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T = executeCatching {
        val authenticationMetadata = authenticator.authenticateWithApple()

        return exchangeFirebaseToken(
            authenticationMetadata.idToken,
            authenticationMetadata.profileMetadata,
            userQueryBuilder,
            shouldUpdateTimeZone
        )
    }

    @Throws(Throwable::class)
    suspend fun sendSignInLinkToEmail(email: String) = executeCatching {
        authenticator.sendEmailLink(email)
    }

    @Throws(Throwable::class)
    suspend fun resendSignInLink() = executeCatching {
        authenticator.resendSignInLink()
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> signInWithEmailLink(
        emailLink: String,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T = executeCatching {
        val idToken = authenticator.verifyEmailLink(emailLink)

        return exchangeFirebaseToken(
            idToken.orEmpty(),
            null,
            userQueryBuilder,
            shouldUpdateTimeZone
        )
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> exchangeFirebaseToken(
        idToken: String,
        profileMetadata: ProfileMetadata? = null,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T = executeCatching {
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

        sharedPreference.putBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
        clearCachedCredentialsData()
        return updatedUser
    }

    @Throws(Throwable::class)
    suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        phoneVerificationProvider: PhoneVerificationProvider
    ): PhoneVerificationMetadata = executeCatching {
        return authenticator.sendPhoneVerification(phoneNumber, phoneVerificationProvider)
    }

    @Throws(Throwable::class)
    suspend fun resendVerificationCode(phoneVerificationProvider: PhoneVerificationProvider): PhoneVerificationMetadata = executeCatching {
        return authenticator.resendVerificationCode(phoneVerificationProvider)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> verifyPhoneNumber(
        otp: String,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
        shouldUpdateTimeZone: Boolean = true
    ): T = executeCatching {
        val idToken = authenticator.verifyPhoneVerification(otp)

        return exchangeFirebaseToken(
            idToken,
            null,
            userQueryBuilder,
            shouldUpdateTimeZone
        )
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> linkPhoneNumber(otp: String) = executeCatching {
        authenticator.linkPhoneNumber(otp)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreference.getSecureString(SharedConstants.ACCESS_TOKEN).isNullOrEmpty().not()
    }

    suspend fun signOut() = executeCatching {
        logoutUseCase.logout()
    }

    fun saveUserToken(token: String) {
        sharedPreference.putSecureString(SharedConstants.ACCESS_TOKEN, token)
    }

    fun clearCachedCredentialsData() {
        sharedPreference.clearSecureValue(SharedConstants.SIGN_IN_EMAIL_LINK_EMAIL)
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER)
        sharedPreference.clearSecureValue(SharedConstants.VERIFICATION_PHONE_NUMBER_VERIFICATION_ID)
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> overrideCurrentUser(
        userId: Int,
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
    ): T = executeCatching {
        val response = authService.post<AuthResponse<T>> {
            endpoint("/backdoor")
            authenticated(false)
            body(OverrideUserRequest(userId))
        }

        saveUserToken(response.jwt.orEmpty())

        val updatedUser = userRepository.getCurrentUser<T>(
            forceUpdate = true,
            userQueryBuilder = userQueryBuilder
        )

        // Disable analytics tracking for override user to avoid tracking the override user
        sharedPreference.putBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, false)

        return updatedUser
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> clearOverrideUserAndResetCurrentUser(
        noinline userQueryBuilder: StrapiQueryBuilder.() -> Unit = {},
    ): T = executeCatching {
        val user: T = signInWithCurrentIdToken(userQueryBuilder)
        // Enable analytics tracking after resetting the user
        sharedPreference.putBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
        return user
    }
}