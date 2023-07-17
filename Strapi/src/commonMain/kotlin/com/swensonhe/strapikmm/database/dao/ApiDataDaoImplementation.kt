package com.swensonhe.strapikmm.database.dao

import com.swensonhe.strapikmm.database.LocalApiData
import com.swensonhe.caching.datasource.database.sqldelight.ApiDataQueries
import com.swensonhe.strapikmm.util.toBoolean
import com.swensonhe.strapikmm.util.toInt


class ApiDataDaoImplementation(private val apiDataQueries: ApiDataQueries) : ApiDataDao {
    override suspend fun getDataByModelVersionAndApiName(
        modelVersion: Int,
        apiName: String
    ): LocalApiData? {
        val localData =
            apiDataQueries.getDataByModelVersionAndApiName(modelVersion.toLong(), apiName)
                .executeAsOneOrNull()
                ?: return null
        return LocalApiData(
            apiName = localData.apiName,
            modelVersion = localData.modelVersion.toInt(),
            content = localData.content,
            modelName = localData.modelName,
            isList = localData.isList?.toInt()?.toBoolean() ?: false
        )
    }

    override suspend fun getDataByApiName(apiName: String): LocalApiData? {
        val localData = apiDataQueries.getDataByApiName(apiName).executeAsOneOrNull()
            ?: return null
        return LocalApiData(
            apiName = localData.apiName,
            modelVersion = localData.modelVersion.toInt(),
            content = localData.content,
            modelName = localData.modelName,
            isList = localData.isList?.toInt()?.toBoolean() ?: false
        )
    }

    override suspend fun getAllData(): List<LocalApiData> {
        val localData = apiDataQueries.getAllData().executeAsList()
        return localData.map {
            LocalApiData(
                apiName = it.apiName,
                modelVersion = it.modelVersion.toInt(),
                content = it.content,
                modelName = it.modelName,
                isList = it.isList?.toInt()?.toBoolean() ?: false
            )
        }
    }

    override suspend fun getAllListDataByModelVersionAndModelName(
        modelVersion: Int,
        modelName: String
    ): List<LocalApiData> {
        val localData = apiDataQueries.getAllListDataByModelVersionAndModelName(modelVersion.toLong(), modelName).executeAsList()
        return localData.map {
            LocalApiData(
                apiName = it.apiName,
                modelVersion = it.modelVersion.toInt(),
                content = it.content,
                modelName = it.modelName,
                isList = it.isList?.toInt()?.toBoolean() ?: false
            )
        }
    }

    override suspend fun deleteDataByModelVersionAndApiName(modelVersion: Int, apiName: String) {
        apiDataQueries.deleteDataByModelVersionAndApiName(modelVersion.toLong(), apiName)
    }

    override suspend fun deleteDataByApiName(apiName: String) {
        apiDataQueries.deleteDataByApiName(apiName)
    }

    override suspend fun deleteAllData() {
        apiDataQueries.deleteAllData()
    }

    override suspend fun insertOrUpdateData(
        apiName: String,
        modelVersion: Int,
        data: String,
        modelName: String,
        isList: Boolean
    ) {
        apiDataQueries.insertOrUpdateData(
            apiName,
            modelVersion.toLong(),
            data,
            modelName,
            isList.toInt().toLong()
        )
    }
}