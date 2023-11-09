import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id(Plugins.Android.ANDROID_LIBRARY)
    kotlin(Plugins.Kotlin.MULTIPLATFORM)
    kotlin(Plugins.Kotlin.SERIALIZATION) version StrapiLibraryVersions.Kotlin.VERSION
    kotlin(Plugins.COCOAPODS)
    id(Plugins.MAVEN_PUBLISH)
    id(Plugins.SIGNING)
    id(Plugins.SQL_DELIGHT)
    id(Plugins.SWIFT_KLIB) version Plugins.Version.SWIFT_KLIB
}

val publishGroupId: String = gradleLocalProperties(rootDir).getProperty("publishGroupId") as String
val publishEmail: String = gradleLocalProperties(rootDir).getProperty("publishEmail") as String
val publishRepository: String = gradleLocalProperties(rootDir).getProperty("publishRepository") as String
val publishDeveloper: String = gradleLocalProperties(rootDir).getProperty("publishDeveloper") as String

val versionProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "versions.properties")))
}

val currentVersion = versionProperties.getProperty("PUBLISH_VERSION") as String
val libName = "strapiKMM"

version = currentVersion

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    cocoapods {
        version = "1.0.0"
        summary = "Shared Module for Strapi KMM"
        homepage = "https://github.com/swensonhe/strapi-kmm"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")

        pod(StrapiLibraryDependencies.iOS.Firebase.AUTH, linkOnly = true)
        pod(StrapiLibraryDependencies.iOS.Firebase.DYNAMIC_LINKS)
        pod(StrapiLibraryDependencies.iOS.Google.SIGN_IN)
        pod(StrapiLibraryDependencies.iOS.Amplitude.SDK)
        pod(StrapiLibraryDependencies.iOS.CleverTap.SDK) {
            moduleName = StrapiLibraryDependencies.iOS.CleverTap.MODULE
        }

        framework {
            baseName = libName + "pods" // To differentiate from the XCFramework name
            isStatic = true
        }
    }

    android {
        publishLibraryVariants("debug", "release")
    }

    val xcf = XCFramework(libName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework(libName) {
            baseName = libName
            isStatic = true
            xcf.add(this)
        }

        it.compilations {
            val main by getting {
                cinterops {
                    create("ContactsDataCollector")
                    create("BackgroundDownloader")
                }
            }
        }
    }

    swiftklib {
        create("ContactsDataCollector") {
            path = file("src/iosMain/native/contactsdatacollector")
            packageName("com.swensonhe.strapikmm.common.contacts.contactsdatacollector")
        }
        create("BackgroundDownloader") {
            path = file("src/iosMain/native/backgrounddownloader")
            packageName("com.swensonhe.strapikmm.common.downloader.backgrounddownloader")
        }
    }

    js(IR) {
        nodejs()
    }

    metadata {
        compilations.matching { it.name == "iosMain" }.all {
            compileKotlinTaskProvider.configure { enabled = false }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(StrapiLibraryDependencies.Ktor.CORE)
                api(StrapiLibraryDependencies.Ktor.CLIENT_SERALIZATION)
                api(StrapiLibraryDependencies.Ktor.SERIALIZATION)
                api(StrapiLibraryDependencies.Ktor.CONTENT_NEGOTIATION)
                api(StrapiLibraryDependencies.Ktor.LOGBACK)
                api(StrapiLibraryDependencies.Ktor.LOGGING)
                api(StrapiLibraryDependencies.SharedPreferences.SETTINGS)
                api(StrapiLibraryDependencies.DateTime.LIB)
                api(StrapiLibraryDependencies.Firebase.LIB)
            }
        }
        val androidMain by getting {
            dependencies {
                api(StrapiLibraryDependencies.Ktor.ANDROID)
                api(StrapiLibraryDependencies.SqlDelight.ANDROID_DRIVER)
                implementation(StrapiLibraryDependencies.Android.Crypto.LIB)
                implementation(StrapiLibraryDependencies.Android.Activity.KTX)
                implementation(StrapiLibraryDependencies.Android.Google.PlayServices.AUTH)

                implementation(StrapiLibraryDependencies.Android.Firebase.BOM)
                implementation(StrapiLibraryDependencies.Android.Firebase.DYNAMIC_LINKS)
                implementation(StrapiLibraryDependencies.Android.InstallReferrer.LIB)
                implementation(StrapiLibraryDependencies.Android.CleverTap.SDK)
                implementation(StrapiLibraryDependencies.Android.Amplitude.SDK)
                implementation(StrapiLibraryDependencies.Android.Contacts.SDK)
                implementation(StrapiLibraryDependencies.Android.Contacts.COROUTINES)
                implementation(StrapiLibraryDependencies.Android.FetchDownloader.SDK)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(StrapiLibraryDependencies.Ktor.IOS)
                api(StrapiLibraryDependencies.SqlDelight.NATIVE_DRIVER)
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(StrapiLibraryDependencies.Ktor.JS)
                api(StrapiLibraryDependencies.Ktor.JS_SERIALIZATION)
                api(StrapiLibraryDependencies.SqlDelight.JS_DRIVER)
                api(npm(StrapiLibraryDependencies.SqlDelight.SQL_JS, StrapiLibraryVersions.SqlDelight.SQL_JS))
                api(devNpm(StrapiLibraryDependencies.SqlDelight.COPY_WEBPACK_PLUGIN, StrapiLibraryVersions.SqlDelight.COPY_WEBPACK_PLUGIN))
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = StrapiLibraryVersions.Java.VERSION.toString()
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.swensonhe.caching.datasource.database")
            generateAsync.set(true)
        }
    }
}

android {
    namespace = "com.swensonhe.strapiKMM"
    compileSdk = AndroidVersions.COMPILE_SDK
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidVersions.MIN_SDK
        targetSdk = AndroidVersions.TARGET_SDK
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = StrapiLibraryVersions.Java.VERSION
        targetCompatibility = StrapiLibraryVersions.Java.VERSION
    }
}


group = publishGroupId
version = currentVersion

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = project.group.toString()
    }
}

publishing {
    repositories {
        repositories {
            maven("https://maven.pkg.github.com/swensonhe/strapi-kmm") {
                name = "Github"
                credentials {
                    username = gradleLocalProperties(rootDir).getProperty("PUBLISH_REPO_USER") as String
                    password = gradleLocalProperties(rootDir).getProperty("PUBLISH_REPO_TOKEN") as String
                }
            }
        }
    }

    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }
    publications.withType<MavenPublication> {

        artifact(javadocJar)

        pom {
            name.set("strapi-kmm")
            description.set("Shared KMM Module")
            url.set(publishRepository)

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            scm {
                connection.set(publishRepository)
                url.set(publishRepository)
            }
            developers {
                developer {
                    name.set(publishDeveloper)
                    email.set(publishEmail)
                }
            }
        }
    }
}