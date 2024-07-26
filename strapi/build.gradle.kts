import co.touchlab.skie.configuration.EnumInterop
import co.touchlab.skie.configuration.FunctionInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    kotlin(Plugins.cocoapods)
    id(Plugins.mavenPublish)
    id(Plugins.signing)
    id(Plugins.SQL_DELIGHT)
    id(Plugins.SWIFT_KLIB) version Plugins.Version.SWIFT_KLIB
    id(Plugins.SKIE) version Plugins.Version.SKIE
}

val publishGroupId: String = project.property("publishGroupId") as String
val publishEmail: String = project.property("publishEmail") as String
val publishRepository: String = project.property("publishRepository") as String
val publishDeveloper: String = project.property("publishDeveloper") as String

val versionProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "versions.properties")))
}

val currentVersion = versionProperties.getProperty("PUBLISH_VERSION") as String
val libName = "strapiKMM"

version = currentVersion

skie {
    features {
        group {
            EnumInterop.Enabled(true)
            SealedInterop.Enabled(true)
            FunctionInterop.FileScopeConversion.Enabled(true)
            SuspendInterop.Enabled(true)
        }
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()


    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")

        pod("FirebaseAuth", linkOnly = true)
        pod("GoogleSignIn")
        pod("FirebaseDynamicLinks")
        pod("Amplitude")
        pod("CleverTap-iOS-SDK") {
            moduleName = "CleverTapSDK"
        }

        framework {
            baseName = libName + "pods" // DON'T CHANGE THIS LINE, there is a bug in the plugin that requires unique names for each framework
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
                    create("GZipDecompressor")
                    create("DESEncryption")
                }
            }
        }
    }

    swiftklib {
        create("ContactsDataCollector") {
            path = file("src/iosMain/native/contactsdatacollector")
            packageName("com.metacto.strapikmm.common.contacts.contactsdatacollector")
        }
        create("BackgroundDownloader") {
            path = file("src/iosMain/native/backgrounddownloader")
            packageName("com.metacto.strapikmm.common.downloader.backgrounddownloader")
        }
        create("GZipDecompressor") {
            path = file("src/iosMain/native/gzipdecompressor")
            packageName("com.metacto.strapikmm.common.gzip.decompressor")
        }
        create("DESEncryption") {
            path = file("src/iosMain/native/desencryption")
            packageName("com.metacto.strapikmm.common.encryption")
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
                api(Ktor.core)
                api(Ktor.clientSerialization)
                api(Ktor.ktorKotlinSerialization)
                api(Ktor.contentNegotiation)
                api(Ktor.logback)
                api(Ktor.logging)
                api(ProjectDependencies.MULTIPLATFORM_SETTINGS)
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                api("dev.gitlive:firebase-auth:1.12.0-metacto-13")

                api("dev.gitlive:firebase-config:1.10.4")
            }
        }
        val androidMain by getting {
            val contactsVersion = "1.4.0"
            val fetchVersion = "3.1.6"
            val awsS3Version = "2.73.0"

            dependencies {
                implementation("androidx.security:security-crypto:1.0.0")
                api(Ktor.android)
                api(ProjectDependencies.SqlDelight.ANDROID_DRIVER)
                implementation("androidx.activity:activity-ktx:1.7.2")
                implementation("com.google.android.gms:play-services-auth:20.7.0")
                implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
                implementation("com.google.firebase:firebase-dynamic-links-ktx")
                api("com.amplitude:android-sdk:2.39.8")
                api("com.clevertap.android:clevertap-android-sdk:6.0.0")
                api("com.android.installreferrer:installreferrer:2.2")

                implementation("com.alexstyl:contactstore:$contactsVersion")
                implementation("com.alexstyl:contactstore-coroutines:$contactsVersion")

                implementation("androidx.tonyodev.fetch2:xfetch2:$fetchVersion")
                api("com.amazonaws:aws-android-sdk-s3:$awsS3Version")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(Ktor.ios)
                api(ProjectDependencies.SqlDelight.NATIVE_DRIVER)
                api("com.rickclephas.kmp:nserror-kt:0.2.0") // Mapping throwable to NSError
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(Ktor.js)
                api(Ktor.jsSeralization)
                api(ProjectDependencies.SqlDelight.JS_DRIVER)
                api(npm(ProjectDependencies.SqlDelight.SQL_JS, ProjectDependencies.SQL_JS))
                api(devNpm(ProjectDependencies.SqlDelight.COPY_WEBPACK_PLUGIN, ProjectDependencies.COPY_WEBPACK_PLUGIN))
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    task("testClasses")
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.metacto.caching.datasource.database")
            generateAsync.set(true)
        }
        linkSqlite.set(true)
    }
}

android {
    namespace = "com.metacto.strapiKMM"
    compileSdk = Application.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            maven("https://maven.pkg.github.com/Meta-CTO/strapi-kmm") {
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
