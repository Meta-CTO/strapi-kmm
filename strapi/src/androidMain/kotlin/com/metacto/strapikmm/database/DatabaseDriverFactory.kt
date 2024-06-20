package com.metacto.strapikmm.database

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.metacto.caching.datasource.database.AppDatabase
import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.errorhandling.ErrorMapper

actual class DatabaseDriverFactory actual constructor(private val context: Any?) {
    init {
        if (context == null || context !is Context) {
            throw ErrorMapper.mapToAppException(
                "Context must be an Android Context",
                -1
            )
        }
    }

    actual suspend fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            AppDatabase.Schema.synchronous(),
            context!! as Context,
            SharedConstants.APP_DATABASE_NAME
        )
    }
}