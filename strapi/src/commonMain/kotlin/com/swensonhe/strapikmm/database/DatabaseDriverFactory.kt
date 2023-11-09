package com.swensonhe.strapikmm.database

import app.cash.sqldelight.db.SqlDriver

/**
 * An expect class for creating database driver instances for database connectivity.
 *
 * @param context A platform-specific context or configuration object, if required for driver creation.
 */
expect class DatabaseDriverFactory(context: Any?) {
    /**
     * Creates a database driver instance for database connectivity. The implementation
     * of this method is platform-specific.
     *
     * @return A database driver instance for the specific platform.
     */
    suspend fun createDriver(): SqlDriver
}
