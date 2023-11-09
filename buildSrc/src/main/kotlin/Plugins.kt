object Plugins {
    const val MAVEN_PUBLISH = "maven-publish"
    const val SIGNING = "signing"
    const val COCOAPODS = "native.cocoapods"
    const val SWIFT_KLIB = "io.github.ttypic.swiftklib"
    const val SQL_DELIGHT = "app.cash.sqldelight"


    object Kotlin {
        const val MULTIPLATFORM = "multiplatform"
        const val SERIALIZATION = "plugin.serialization"
        const val ANDROID = "android"
        const val GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:${StrapiLibraryVersions.Kotlin.VERSION}"
    }

    object Android {
        const val ANDROID_LIBRARY = "com.android.library"
        const val MULTIPLATFORM_ANDROID = "org.jetbrains.kotlin.android"
        const val BUILD_TOOLS = "com.android.tools.build:gradle:${AndroidVersions.BUILD_TOOLS}"
    }

    object Version {
        const val SWIFT_KLIB = "0.3.0"
    }
}

