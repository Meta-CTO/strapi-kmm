import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.addRootDependencies(
    credentialUserName: String?= null,
    credentialPassword: String?= null
) {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://maven.pkg.github.com/swensonhe/firebase-kotlin-sdk") {
        if(credentialUserName != null && credentialPassword != null) {
            credentials {
                username = credentialUserName
                password = credentialPassword
            }
        }
    }
    jcenter()
    mavenLocal()
    maven(url = "https://jitpack.io")
}