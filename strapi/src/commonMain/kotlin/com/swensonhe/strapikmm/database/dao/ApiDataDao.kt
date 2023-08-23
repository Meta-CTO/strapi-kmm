//package com.swensonhe.strapikmm.database.dao
//
//import com.swensonhe.strapikmm.database.ContentData
//
//interface ApiDataDao {
//    suspend fun getListDataByModelVersionAndApiUrl(
//        modelVersion: Int,
//        apiUrl: String
//    ): List<ContentData>?
//
//    suspend fun getListDataByModelVersionAndModelType(
//        modelVersion: Int,
//        modelType: String
//    ): List<ContentData>?
//
//    suspend fun getListDataByApiUrl(apiUrl: String): List<ContentData>?
//    suspend fun getListDataByModelType(modelType: String): List<ContentData>?
//    suspend fun insertOrUpdateListData(
//        apiUrl: String,
//        modelVersion: Int,
//        modelType: String,
//        content: String
//    )
//
//    suspend fun deleteListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String)
//    suspend fun deleteListDataByApiUrl(apiUrl: String)
//    suspend fun getContentDataByModelVersionAndModelType(
//        modelVersion: Int,
//        modelType: String
//    ): List<ContentData>?
//    suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(
//        modelVersion: Int,
//        modelType: String,
//        apiUrl: String
//    ): ContentData?
//
//    suspend fun getContentDataByModelVersionAndApiUrl(
//        modelVersion: Int,
//        apiUrl: String
//    ): ContentData?
//
//    suspend fun getContentDataByModelType(modelType: String): List<ContentData>?
//    suspend fun getContentDataByApiUrl(apiUrl: String): ContentData?
//    suspend fun insertOrUpdateContentData(
//        modelId: Int?,
//        modelType: String?,
//        modelVersion: Int?,
//        content: String?,
//        apiUrl: String?
//    )
//
//    suspend fun deleteContentDataByModelVersionAndModelType(modelVersion: Int, modelType: String)
//    suspend fun deleteContentDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String)
//    suspend fun deleteContentDataByModelType(modelType: String)
//    suspend fun deleteContentDataByApiUrl(apiUrl: String)
//    suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String)
//    suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(
//        modelType: String,
//        modelVersion: Int,
//        modelIds: List<Int>
//    ): List<ContentData>?
//    suspend fun getContentDataByModelTypeAndModelVersionAndModelId(
//        modelType: String,
//        modelVersion: Int,
//        modelId: Int
//    ): ContentData?
//}