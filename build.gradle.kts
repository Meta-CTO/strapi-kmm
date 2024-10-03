buildscript {
    repositories {
        gradlePluginPortal()
        google()
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
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath(ProjectDependencies.SqlDelight.PLUGIN)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/Meta-CTO/firebase-kotlin-sdk")
            credentials {
                username = "metactoengineer"
                password = "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"
            }
        }
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        jcenter()
        mavenLocal()
        maven(url = "https://jitpack.io")

    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}