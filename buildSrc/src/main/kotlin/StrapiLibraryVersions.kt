import org.gradle.api.JavaVersion

object StrapiLibraryVersions {
    object Java {
        val VERSION = JavaVersion.VERSION_17
    }


    object SqlDelight {
        const val VERSION = "2.0.0-rc01"
        const val COPY_WEBPACK_PLUGIN = "9.1.0"
        const val SQL_JS = "1.6.2"
    }

    object SharedPreferences {
        const val SETTINGS = "1.0.0"
    }

    object Kotlin {
        const val VERSION = "1.9.10"
        const val COROUTINES = "1.7.1-native-mt"
    }

    object Ktor {
        const val VERSION = "2.3.2"
        const val LOG_BACK = "1.2.10"
        const val SERILIZATION = "1.3.2"
    }

    object DateTime {
        const val VERSION = "0.4.0"
    }

    object Firebase {
        const val VERSION = "1.8.2-swensonhe"
    }

    object Android {
        object InstallReferrer {
            const val LIB = "2.2"
        }

        object Crypto {
            const val LIB = "1.0.0"
        }

        object Activity {
            const val KTX = "1.8.0"
        }

        object Google {
            object PlayServices {
                const val AUTH = "20.7.0"
            }
        }

        object Firebase {
            const val BOM = "32.1.1"
        }

        object Amplitude {
            const val SDK = "2.39.8"
        }

        object CleverTap {
            const val SDK = "5.2.0"
        }

        object Contacts {
            const val VERSION = "1.4.0"
        }

        object FetchDownloader {
            const val VERSION = "3.1.6"
        }
    }
}