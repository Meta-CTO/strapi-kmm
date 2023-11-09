object StrapiLibraryDependencies {

    object SharedPreferences {
        const val SETTINGS = "com.russhwolf:multiplatform-settings:${StrapiLibraryVersions.SharedPreferences.SETTINGS}"
    }

    object SqlDelight {
        const val PLUGIN = "app.cash.sqldelight:gradle-plugin:${StrapiLibraryVersions.SqlDelight.VERSION}"
        const val ANDROID_DRIVER = "app.cash.sqldelight:android-driver:${StrapiLibraryVersions.SqlDelight.VERSION}"
        const val NATIVE_DRIVER = "app.cash.sqldelight:native-driver:${StrapiLibraryVersions.SqlDelight.VERSION}"
        const val JS_DRIVER = "app.cash.sqldelight:web-worker-driver:${StrapiLibraryVersions.SqlDelight.VERSION}"
        const val COPY_WEBPACK_PLUGIN = "copy-webpack-plugin"
        const val SQL_JS = "sql.js"
    }

    object Ktor {
        const val CORE = "io.ktor:ktor-client-core:${StrapiLibraryVersions.Ktor.VERSION}"
        const val CLIENT_SERALIZATION = "io.ktor:ktor-client-serialization:${StrapiLibraryVersions.Ktor.VERSION}"
        const val CONTENT_NEGOTIATION = "io.ktor:ktor-client-content-negotiation:${StrapiLibraryVersions.Ktor.VERSION}"
        const val SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json:${StrapiLibraryVersions.Ktor.VERSION}"
        const val LOGGING = "io.ktor:ktor-client-logging:${StrapiLibraryVersions.Ktor.VERSION}"
        const val LOGBACK = "ch.qos.logback:logback-classic:${StrapiLibraryVersions.Ktor.LOG_BACK}"
        const val ANDROID = "io.ktor:ktor-client-android:${StrapiLibraryVersions.Ktor.VERSION}"
        const val JS = "io.ktor:ktor-client-js:${StrapiLibraryVersions.Ktor.VERSION}"
        const val JS_SERIALIZATION = "io.ktor:ktor-client-serialization-js:${StrapiLibraryVersions.Ktor.VERSION}"
        const val IOS = "io.ktor:ktor-client-ios:${StrapiLibraryVersions.Ktor.VERSION}"
    }

    object DateTime {
        const val LIB = "org.jetbrains.kotlinx:kotlinx-datetime:${StrapiLibraryVersions.DateTime.VERSION}"
    }

    object Firebase {
        const val LIB = "dev.gitlive:firebase-auth:${StrapiLibraryVersions.Firebase.VERSION}"
    }

    object Android {
        object Crypto {
            const val LIB = "androidx.security:security-crypto:${StrapiLibraryVersions.Android.Crypto.LIB}"
        }

        object InstallReferrer {
            const val LIB = "com.android.installreferrer:installreferrer:${StrapiLibraryVersions.Android.InstallReferrer.LIB}"
        }

        object Activity {
            const val KTX = "androidx.activity:activity-ktx:${StrapiLibraryVersions.Android.Activity.KTX}"
        }

        object Google {
            object PlayServices {
                const val AUTH = "com.google.android.gms:play-services-auth:${StrapiLibraryVersions.Android.Google.PlayServices.AUTH}"
            }
        }

        object Firebase {
            const val BOM = "com.google.firebase:firebase-bom:${StrapiLibraryVersions.Android.Firebase.BOM}"
            const val DYNAMIC_LINKS = "com.google.firebase:firebase-dynamic-links-ktx"
        }

        object Amplitude {
            const val SDK = "com.amplitude:android-sdk:${StrapiLibraryVersions.Android.Amplitude.SDK}"
        }

        object CleverTap {
            const val SDK = "com.clevertap.android:clevertap-android-sdk:${StrapiLibraryVersions.Android.CleverTap.SDK}"
        }

        object Contacts {
            const val SDK = "com.alexstyl:contactstore:${StrapiLibraryVersions.Android.Contacts.VERSION}"
            const val COROUTINES = "com.alexstyl:contactstore-coroutines:${StrapiLibraryVersions.Android.Contacts.VERSION}"
        }

        object FetchDownloader {
            const val SDK = "androidx.tonyodev.fetch2:xfetch2:${StrapiLibraryVersions.Android.FetchDownloader.VERSION}"
        }
    }


    object iOS {
        object Firebase {
            const val AUTH = "FirebaseAuth"
            const val DYNAMIC_LINKS = "FirebaseDynamicLinks"
        }

        object Google {
            const val SIGN_IN = "GoogleSignIn"
        }

        object Amplitude {
            const val SDK = "Amplitude"
        }

        object CleverTap {
            const val MODULE = "CleverTapSDK"
            const val SDK = "CleverTap-iOS-SDK"
        }
    }
}