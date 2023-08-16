pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven {
            url = uri("https://maven.pkg.github.com/swensonhe/firebase-kotlin-sdk")
            credentials {
//                // TODO: Remove this before publishing to our repo
                username = "developer-swensonhe"
                password = "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"
            }
        }
    }
}

rootProject.name = "StrapiKMM"
include(":Strapi")
include(":app")
