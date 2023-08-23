import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    kotlin(Plugins.cocoapods)
    id(Plugins.mavenPublish)
    id(Plugins.signing)
//    id(Plugins.SQL_DELIGHT)
}

val publishKey: String = gradleLocalProperties(rootDir).getProperty("publishKey")
val publishSecret: String = gradleLocalProperties(rootDir).getProperty("publishSecret")
val publishUsername: String = gradleLocalProperties(rootDir).getProperty("publishUsername")
val publishPassword: String = gradleLocalProperties(rootDir).getProperty("publishPassword")
val publishGroupId: String = gradleLocalProperties(rootDir).getProperty("publishGroupId")
val publishEmail: String = gradleLocalProperties(rootDir).getProperty("publishEmail")
val publishRepository: String = gradleLocalProperties(rootDir).getProperty("publishRepository")
val publishDeveloper: String = gradleLocalProperties(rootDir).getProperty("publishDeveloper")

val currentVersion = "7.3.3"
val libName = "strapiKMM"

version = currentVersion

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()


    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
//        noPodspec()
        pod("FirebaseAuth", linkOnly = true)
        pod("GoogleSignIn")
        pod("FirebaseDynamicLinks")
        framework {
            baseName = libName
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
                api(ProjectDependencies.sharedPreferencesMultiplatformSettings)
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                api("dev.gitlive:firebase-auth:1.8.2-swensonhe")

//                api("io.github.luca992.libphonenumber-kotlin:libphonenumber:0.1.0-SNAPSHOT")
//                implementation("dev.icerock.moko:resources:0.23.0")
//                implementation("co.touchlab:kermit:2.0.0-RC5")
//                implementation("co.touchlab:kermit:2.0.0-RC5")


            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.security:security-crypto:1.0.0")
                api(Ktor.android)
//                api(ProjectDependencies.SqlDelight.ANDROID_DRIVER)
                implementation("androidx.activity:activity-ktx:1.7.2")
                implementation("com.google.android.gms:play-services-auth:20.6.0")
                implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
                implementation("com.google.firebase:firebase-dynamic-links-ktx")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(Ktor.ios)
//                api(ProjectDependencies.SqlDelight.NATIVE_DRIVER)
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
//                api(ProjectDependencies.SqlDelight.JS_DRIVER)
//
//                api(npm(ProjectDependencies.SqlDelight.SQL_JS, ProjectDependencies.SQL_JS))
//                api(devNpm(ProjectDependencies.SqlDelight.COPY_WEBPACK_PLUGIN, ProjectDependencies.COPY_WEBPACK_PLUGIN))
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    groupId = publishGroupId
                    artifactId = libName.toLowerCase()
                    version = currentVersion

                    from(components.getByName("release"))
                }
                create<MavenPublication>("debug") {
                    groupId = publishGroupId
                    artifactId = "${libName.toLowerCase()}-debug"
                    version = currentVersion

                    from(components.getByName("debug"))
                }
            }
        }
    }
}

//sqldelight {
//    databases {
////        linkSqlite.set(false)
//        create("AppDatabase") {
//            packageName.set("com.swensonhe.caching.datasource.database")
//            generateAsync.set(true)
//        }
//    }
//}

android {
    compileSdkVersion(Application.compileSdk)
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
//        maven {
//            name = "oss"
//            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//
//            credentials {
//                username = publishUsername
//                password = publishPassword
//            }
//        }

        repositories {
            maven("https://maven.pkg.github.com/swensonhe/kmm-internal") {
                name = "Github"
                credentials {
                    username = gradleLocalProperties(rootDir).getProperty("githubRepoUser") as String
                    password = gradleLocalProperties(rootDir).getProperty("githubRepoToken") as String
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

//signing {
//    useInMemoryPgpKeys(publishKey,publishSecret, publishUsername)
//    sign(publishing.publications)
//}

//afterEvaluate {
//    val compilation = kotlin.targets["metadata"].compilations["iosMain"]
//    compilation.compileKotlinTask.doFirst {
//        compilation.compileDependencyFiles = files(
//            compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
//        )
//    }
//}
