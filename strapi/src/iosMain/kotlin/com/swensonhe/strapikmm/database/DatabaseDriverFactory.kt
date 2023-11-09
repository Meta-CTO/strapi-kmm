package com.swensonhe.strapikmm.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.swensonhe.caching.datasource.database.AppDatabase
import com.swensonhe.strapikmm.constants.SharedConstants


/**
 * Creates a [SqlDriver] instance for sqlDelight driver on iOS platform.
 *
 * @param context The application context or platform-specific context object which is not used on iOS.
 */
actual class DatabaseDriverFactory actual constructor(private val context: Any?) {
    actual suspend fun createDriver(): SqlDriver {
        // Create the native driver instance.
        return NativeSqliteDriver(AppDatabase.Schema.synchronous(), SharedConstants.APP_DATABASE_NAME)
    }
}