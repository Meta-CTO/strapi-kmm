package com.swensonhe.strapikmm.database

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.swensonhe.caching.datasource.database.AppDatabase
import com.swensonhe.strapikmm.constants.SharedConstants

/**
 * Creates a [SqlDriver] instance for sqlDelight driver on Android platform.
 *
 * @param context The Android context used to initialize the driver.
 */
actual class DatabaseDriverFactory actual constructor(private val context: Any?) {
    // Validate context is not null and is an Android context
    init {
        if (context == null || context !is Context) {
            throw IllegalStateException("Context must not be null")
        }
    }

    actual suspend fun createDriver(): SqlDriver {
        // Create a new AndroidSqliteDriver instance
        return AndroidSqliteDriver(
            AppDatabase.Schema.synchronous(),
            context!! as Context,
            SharedConstants.APP_DATABASE_NAME
        )
    }
}