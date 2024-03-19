package com.metaCTO.strapikmm.database

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory(context: Any?) {
    suspend fun createDriver(): SqlDriver
}