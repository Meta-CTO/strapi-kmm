package com.swensonhe.strapikmm.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.swensonhe.caching.datasource.database.AppDatabase
import org.w3c.dom.Worker

actual class DatabaseDriverFactory actual constructor(private val context: Any?) {
    actual suspend fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
            )
        ).also {
            AppDatabase.Schema.create(it).await()
        }
    }
}

