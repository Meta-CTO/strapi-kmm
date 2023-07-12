package com.swensonhe.strapikmm.database

import com.swensonhe.caching.datasource.database.AppDatabase
import com.swensonhe.strapikmm.database.dao.ApiDataDao
import com.swensonhe.strapikmm.database.dao.ApiDataDaoImplementation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LocalDataRepository(private val databaseDriverFactory: DatabaseDriverFactory) {
    private val lockMutex = Mutex()
    private lateinit var appDatabase: AppDatabase

    suspend fun getDataByModelVersionAndApiName(modelVersion: Int, apiName: String) =
        provideApiDataDao().getDataByModelVersionAndApiName(modelVersion, apiName)

    suspend fun getDataByApiName(apiName: String) = provideApiDataDao().getDataByApiName(apiName)

    suspend fun getAllData() = provideApiDataDao().getAllData()

    suspend fun getAllListDataByModelVersionAndApiName(modelVersion: Int, modelName: String) =
        provideApiDataDao().getAllListDataByModelVersionAndApiName(modelVersion, modelName)


    suspend fun deleteDataByModelVersionAndApiName(modelVersion: Int, apiName: String) =
        provideApiDataDao().deleteDataByModelVersionAndApiName(modelVersion, apiName)

    suspend fun deleteDataByApiName(apiName: String) =
        provideApiDataDao().deleteDataByApiName(apiName)

    suspend fun deleteAllData() = provideApiDataDao().deleteAllData()

    suspend fun insertOrUpdateData(apiName: String, modelVersion: Int, data: String, modelName: String, isList: Boolean) =
        provideApiDataDao().insertOrUpdateData(apiName, modelVersion, data, modelName, isList)

    private suspend fun provideApiDataDao(): ApiDataDao {
        val database = provideAppDatabase()
        return ApiDataDaoImplementation(database.apiDataQueries)
    }

    private suspend fun provideAppDatabase(): AppDatabase {
        return lockMutex.withLock {
            if (!::appDatabase.isInitialized) {
                appDatabase = AppDatabase(databaseDriverFactory.createDriver())
            }
            appDatabase
        }
    }
}