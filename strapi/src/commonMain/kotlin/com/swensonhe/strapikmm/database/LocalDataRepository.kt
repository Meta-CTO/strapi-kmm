package com.swensonhe.strapikmm.database

import com.swensonhe.caching.datasource.database.AppDatabase
import com.swensonhe.strapikmm.database.dao.ApiDataDao
import com.swensonhe.strapikmm.database.dao.ApiDataDaoImplementation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Repository responsible for handling local data storage and retrieval.
 * This repository interfaces with the local database to manage and retrieve data.
 *
 * @param databaseDriverFactory A factory for creating database drivers to access the local database.
 */
class LocalDataRepository(private val databaseDriverFactory: DatabaseDriverFactory) {
    private val databaseLockMutex = Mutex()
    private val apiDaoLockMutex = Mutex()
    private val lockMutex = Mutex()
    private lateinit var appDatabase: AppDatabase
    private lateinit var apiDataDao: ApiDataDao

    suspend fun insertOrUpdateListData(
        apiUrl: String, modelVersion: Int, modelType: String, content: String
    ) {
        provideApiDataDao().insertOrUpdateListData(apiUrl, modelVersion, modelType, content)
    }

    suspend fun getListDataByModelVersionAndApiUrl(
        modelVersion: Int, apiUrl: String
    ): List<ContentData>? {
        return provideApiDataDao().getListDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun getListDataByModelVersionAndModelType(
        modelVersion: Int, modelType: String
    ): List<ContentData>? {
        return provideApiDataDao().getListDataByModelVersionAndModelType(modelVersion, modelType)
    }

    suspend fun getListDataByApiUrl(apiUrl: String): List<ContentData>? {
        return provideApiDataDao().getListDataByApiUrl(apiUrl)
    }

    suspend fun getListDataByModelType(modelType: String): List<ContentData>? {
        return provideApiDataDao().getListDataByModelType(modelType)
    }

    suspend fun deleteListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String) {
        provideApiDataDao().deleteListDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun deleteListDataByApiUrl(apiUrl: String) {
        provideApiDataDao().deleteListDataByApiUrl(apiUrl)
    }

    suspend fun getContentDataByModelVersionAndModelType(
        modelVersion: Int, modelType: String
    ): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelVersionAndModelType(modelVersion, modelType)
    }

    suspend fun getContentDataByModelVersionAndApiUrl(
        modelVersion: Int, apiUrl: String
    ): ContentData? {
        return provideApiDataDao().getContentDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(
        modelVersion: Int, modelType: String, apiUrl: String
    ): ContentData? {
        return provideApiDataDao().getContentDataByModelVersionAndModelTypeAndApiUrl(
            modelVersion, modelType, apiUrl
        )
    }

    suspend fun getContentDataByModelType(modelType: String): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelType(modelType)
    }

    suspend fun getContentDataByApiUrl(apiUrl: String): ContentData? {
        return provideApiDataDao().getContentDataByApiUrl(apiUrl)
    }

    suspend fun insertOrUpdateContentData(
        modelVersion: Int?, modelType: String?, content: String?, apiUrl: String?, modelId: Int?
    ) {
        provideApiDataDao().insertOrUpdateContentData(
            modelId, modelType, modelVersion, content, apiUrl
        )
    }

    suspend fun deleteContentDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String) {
        provideApiDataDao().deleteContentDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun deleteContentDataByApiUrl(apiUrl: String) {
        provideApiDataDao().deleteContentDataByApiUrl(apiUrl)
    }

    suspend fun deleteContentDataByModelVersionAndModelType(modelVersion: Int, modelType: String) {
        provideApiDataDao().deleteContentDataByModelVersionAndModelType(modelVersion, modelType)
    }

    suspend fun deleteContentDataByModelType(modelType: String) {
        provideApiDataDao().deleteContentDataByModelType(modelType)
    }

    suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(
        modelType: String, modelVersion: Int, modelIds: List<Int>
    ): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelTypeAndModelVersionAndModelIds(
            modelType, modelVersion, modelIds
        )
    }

    suspend fun getContentDataByModelTypeAndModelVersionAndModelId(
        modelType: String, modelVersion: Int, modelId: Int
    ): ContentData? {
        return provideApiDataDao().getContentDataByModelTypeAndModelVersionAndModelId(
            modelType, modelVersion, modelId
        )
    }

    suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String) {
        provideApiDataDao().deleteContentDataByModelIdAndModelType(modelId, modelType)
    }


    private suspend fun provideApiDataDao(): ApiDataDao {
        // Use a mutex to ensure that only one thread can initialize the database or api at a time.
        return apiDaoLockMutex.withLock {
            // If the api dao hasn't been initialized yet, create it.
            if (!::apiDataDao.isInitialized) {
                // Create the api dao.
                apiDataDao = ApiDataDaoImplementation(provideAppDatabase().apiDataQueries)
            }
            apiDataDao
        }
    }


    /**
     * Provides access to the local `AppDatabase`, creating it if it hasn't been initialized.
     *
     * @return The initialized instance of the `AppDatabase`.
     */
    private suspend fun provideAppDatabase(): AppDatabase {
        // Use a mutex to ensure that only one thread can initialize the database at a time.
        return databaseLockMutex.withLock {
            // If the database hasn't been initialized yet, create it.
            if (!::appDatabase.isInitialized) {
                // Create the database driver.
                appDatabase = AppDatabase(databaseDriverFactory.createDriver())
            }
            // Return the initialized database.
            appDatabase
        }
    }
}