import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.classpath(dependencyNotation: Any): Dependency? =
    add("classpath", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: String): Dependency? =
    add("implementation", dependencyNotation)

fun DependencyHandler.implementation(dependency: Dependency): Dependency? =
    add("implementation", dependency)

fun DependencyHandler.debugImplementation(dependencyNotation: Any): Dependency? =
    add("debugImplementation", dependencyNotation)

fun DependencyHandler.releaseImplementation(dependencyNotation: Any): Dependency? =
    add("releaseImplementation", dependencyNotation)

fun DependencyHandler.compileOnly(dependencyNotation: Any): Dependency? =
    add("compileOnly", dependencyNotation)

fun DependencyHandler.runtimeOnly(dependencyNotation: Any): Dependency? =
    add("runtimeOnly", dependencyNotation)

fun DependencyHandler.kapt(dependencyNotation: Any): Dependency? =
    add("kapt", dependencyNotation)

fun DependencyHandler.api(dependencyNotation: Any): Dependency? =
    add("api", dependencyNotation)

fun DependencyHandler.debugApi(dependencyNotation: Any): Dependency? =
    add("debugApi", dependencyNotation)

fun DependencyHandler.releaseApi(dependencyNotation: Any): Dependency? =
    add("releaseApi", dependencyNotation)

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)
