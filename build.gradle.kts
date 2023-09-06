buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven {
            url = uri("https://maven.pkg.github.com/swensonhe/firebase-kotlin-sdk")
            credentials {
                // TODO: Remove this before publishing to our repo
                username = "developer-swensonhe"
                password = "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"
            }
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath(ProjectDependencies.SqlDelight.PLUGIN)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/swensonhe/firebase-kotlin-sdk")
            credentials {
                // TODO: Remove this before publishing to our repo
                username = "developer-swensonhe"
                password = "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"
            }
        }

        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}