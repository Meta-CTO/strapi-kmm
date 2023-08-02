package com.swensonhe.strapikmm.database

import com.swensonhe.caching.datasource.database.AppDatabase
import com.swensonhe.strapikmm.database.dao.ApiDataDao
import com.swensonhe.strapikmm.database.dao.ApiDataDaoImplementation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LocalDataRepository(private val databaseDriverFactory: DatabaseDriverFactory) {
    private val lockMutex = Mutex()
    private lateinit var appDatabase: AppDatabase

    suspend fun insertOrUpdateListData(apiUrl: String, modelVersion: Int, modelType: String, content: String) {
        provideApiDataDao().insertOrUpdateListData(apiUrl, modelVersion, modelType, content)
    }

    suspend fun getListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String): List<ContentData>? {
        return provideApiDataDao().getListDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun getListDataByModelVersionAndModelType(modelVersion: Int, modelType: String): List<ContentData>? {
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

    suspend fun getContentDataByModelVersionAndModelType(modelVersion: Int, modelType: String): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelVersionAndModelType(modelVersion, modelType)
    }

    suspend fun getContentDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String): ContentData? {
        return provideApiDataDao().getContentDataByModelVersionAndApiUrl(modelVersion, apiUrl)
    }

    suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(modelVersion: Int, modelType: String, apiUrl: String): ContentData? {
        return provideApiDataDao().getContentDataByModelVersionAndModelTypeAndApiUrl(modelVersion, modelType, apiUrl)
    }

    suspend fun getContentDataByModelType(modelType: String): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelType(modelType)
    }

    suspend fun getContentDataByApiUrl(apiUrl: String): ContentData? {
        return provideApiDataDao().getContentDataByApiUrl(apiUrl)
    }

    suspend fun insertOrUpdateContentData(modelVersion: Int?, modelType: String?, content: String?, apiUrl: String?, modelId: Int?) {
        provideApiDataDao().insertOrUpdateContentData(modelId, modelType, modelVersion, content, apiUrl)
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

    suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(modelType: String, modelVersion: Int, modelIds: List<Int>): List<ContentData>? {
        return provideApiDataDao().getContentDataByModelTypeAndModelVersionAndModelIds(modelType, modelVersion, modelIds)
    }

    suspend fun getContentDataByModelTypeAndModelVersionAndModelId(modelType: String, modelVersion: Int, modelId: Int): ContentData? {
        return provideApiDataDao().getContentDataByModelTypeAndModelVersionAndModelId(modelType, modelVersion, modelId)
    }

    suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String) {
        provideApiDataDao().deleteContentDataByModelIdAndModelType(modelId, modelType)
    }

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