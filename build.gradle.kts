buildscript {
    repositories {
        addRootDependencies()
    }

    dependencies {
        classpath(Plugins.Kotlin.GRADLE)
        classpath(Plugins.Android.BUILD_TOOLS)
        classpath(StrapiLibraryDependencies.SqlDelight.PLUGIN)
    }
}

allprojects {
    repositories {
        addRootDependencies()
    }
}