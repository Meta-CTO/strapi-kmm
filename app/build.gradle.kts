plugins {
    id(Plugins.Android.ANDROID_LIBRARY)
    id(Plugins.Android.MULTIPLATFORM_ANDROID)
    kotlin(Plugins.Kotlin.SERIALIZATION) version StrapiLibraryVersions.Kotlin.VERSION
}

android {
    namespace = "com.swensonhe.kmmsample"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = StrapiLibraryVersions.Java.VERSION
        targetCompatibility = StrapiLibraryVersions.Java.VERSION
    }
    kotlinOptions {
        jvmTarget = StrapiLibraryVersions.Java.VERSION.toString()
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation(project(mapOf("path" to ":strapi")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}