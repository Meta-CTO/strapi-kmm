object ProjectDependencies {

    private const val sharedPreferencesMultiplatformSettingsVersion = "1.0.0"
    const val sharedPreferencesMultiplatformSettings = "com.russhwolf:multiplatform-settings:${sharedPreferencesMultiplatformSettingsVersion}"

    const val SQL_DELIGHT = "2.0.0-rc01"
    const val COPY_WEBPACK_PLUGIN = "9.1.0"
    const val SQL_JS = "1.6.2"

    object SqlDelight {
        const val PLUGIN = "app.cash.sqldelight:gradle-plugin:${SQL_DELIGHT}"
        const val ANDROID_DRIVER = "app.cash.sqldelight:android-driver:${SQL_DELIGHT}"
        const val NATIVE_DRIVER = "app.cash.sqldelight:native-driver:${SQL_DELIGHT}"
        const val JS_DRIVER = "app.cash.sqldelight:web-worker-driver:${SQL_DELIGHT}"
        const val COPY_WEBPACK_PLUGIN = "copy-webpack-plugin"
        const val SQL_JS = "sql.js"
    }
}