buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/innertech/firebase-kotlin-")
            credentials {
                // TODO: Remove this before publishing to our repo
                username = "developer-swensonhe"
                password = "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"
            }
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath(ProjectDependencies.SqlDelight.PLUGIN)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/innertech/firebase-kotlin-")
            credentials {
                // TODO: Remove this before publishing to our repo
                username = "developer-swensonhe"
                password = "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"
            }
        }
    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}