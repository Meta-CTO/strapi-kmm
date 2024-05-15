object Ktor {
    private const val ktorVersion = "2.3.7"
    private const val logBackVersion = "1.2.10"
    const val core = "io.ktor:ktor-client-core:${ktorVersion}"
    const val clientSerialization = "io.ktor:ktor-client-serialization:${ktorVersion}"
    const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${ktorVersion}"
    const val ktorKotlinSerialization = "io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}"
    const val logging = "io.ktor:ktor-client-logging:${ktorVersion}"
    const val logback = "ch.qos.logback:logback-classic:${logBackVersion}"
    const val android = "io.ktor:ktor-client-android:${ktorVersion}"
    const val js = "io.ktor:ktor-client-js:${ktorVersion}"
    const val jsSeralization = "io.ktor:ktor-client-serialization-js:${ktorVersion}"
    const val ios = "io.ktor:ktor-client-ios:${ktorVersion}"
}