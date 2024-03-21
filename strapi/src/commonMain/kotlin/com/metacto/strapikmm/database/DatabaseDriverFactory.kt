package com.metacto.strapikmm.database

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory(context: Any?) {
    suspend fun createDriver(): SqlDriver
}