pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven {
            url = uri("https://maven.pkg.github.com/Meta-CTO/firebase-kotlin-sdk")
            credentials {
                username = "metactoengineer"
                password = "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"
            }
        }

        jcenter()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "StrapiKMM"
include(":strapi")
include(":app")
include(":ksp")
