package com.swensonhe.strapikmm.database.dao

import com.swensonhe.strapikmm.database.LocalApiData

interface ApiDataDao {
    suspend fun getDataByModelVersionAndApiName(modelVersion: Int, apiName: String): LocalApiData?
    suspend fun getDataByApiName(apiName: String): LocalApiData?
    suspend fun getAllData(): List<LocalApiData>?
    suspend fun getAllListDataByModelVersionAndModelName(modelVersion: Int, modelName: String): List<LocalApiData>
    suspend fun deleteDataByModelVersionAndApiName(modelVersion: Int, apiName: String)
    suspend fun deleteDataByApiName(apiName: String)
    suspend fun deleteAllData()
    suspend fun insertOrUpdateData(apiName: String, modelVersion: Int, data: String, modelName: String, isList: Boolean)
}