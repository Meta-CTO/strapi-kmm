package com.metacto.strapikmm.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.metacto.caching.datasource.database.AppDatabase
import com.metacto.strapikmm.constants.SharedConstants

actual class DatabaseDriverFactory actual constructor(private val context: Any?) {
    actual suspend fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema.synchronous(), SharedConstants.APP_DATABASE_NAME)
    }
}