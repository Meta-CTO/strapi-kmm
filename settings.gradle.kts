
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.github.com/swensonhe/firebase-kotlin-sdk")
        jcenter()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "StrapiKMM"
include(":strapi")
include(":app")
