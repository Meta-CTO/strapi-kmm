package com.swensonhe.strapikmm.database.dao

import com.swensonhe.caching.datasource.database.sqldelight.ApiDataQueries
import com.swensonhe.strapikmm.database.ContentData

class ApiDataDaoImplementation(private val apiDataQueries: ApiDataQueries) : ApiDataDao {

    override suspend fun getListDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): List<ContentData> {
        val listData =
            apiDataQueries.getListDataByModelVersionAndApiUrl(modelVersion.toLong(), apiUrl)
                .executeAsOneOrNull() ?: return emptyList()
        val contentIds = listData.content.split(",").map { it.trim().toLong() }
        return apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
            listData.modelType,
            modelVersion.toLong(),
            contentIds
        )
            .executeAsList()
            .map {
                ContentData(
                    modelId = it.modelId?.toInt(),
                    modelType = it.modelType,
                    modelVersion = it.modelVersion?.toInt(),
                    content = it.content,
                    apiUrl = it.apiUrl
                )
            }
    }

    override suspend fun getListDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>? {
        val listData =
            apiDataQueries.getListDataByModelVersionAndModelType(modelVersion.toLong(), modelType)
                .executeAsOneOrNull()
                ?: return null

        val contentIds = listData.content.split(",").map { it.toLong() }
        return apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
            listData.modelType,
            modelVersion.toLong(),
            contentIds
        )
            .executeAsList()
            .map {
                ContentData(
                    modelId = it.modelId?.toInt(),
                    modelType = it.modelType,
                    modelVersion = it.modelVersion?.toInt(),
                    content = it.content,
                    apiUrl = it.apiUrl
                )
            }
    }

    override suspend fun getListDataByApiUrl(apiUrl: String): List<ContentData>? {
        val listData = apiDataQueries.getListDataByApiUrl(apiUrl).executeAsOneOrNull()
            ?: return null

        val contentIds = listData.content.split(",").map { it.toLong() }
        return apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
            listData.modelType,
            listData.modelVersion,
            contentIds
        )
            .executeAsList()
            .map {
                ContentData(
                    modelId = it.modelId?.toInt(),
                    modelType = it.modelType,
                    modelVersion = it.modelVersion?.toInt(),
                    content = it.content,
                    apiUrl = it.apiUrl
                )
            }
    }

    override suspend fun getListDataByModelType(modelType: String): List<ContentData>? {
        val listData = apiDataQueries.getListDataByModelType(modelType).executeAsList()
        if (listData.isEmpty()) return null

        val contentIds = listData.map { it.content.split(",").map { it.toLong() } }.flatten()
        return apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
            listData.first().modelType,
            listData.first().modelVersion,
            contentIds
        )
            .executeAsList()
            .map {
                ContentData(
                    modelId = it.modelId?.toInt(),
                    modelType = it.modelType,
                    modelVersion = it.modelVersion?.toInt(),
                    content = it.content,
                    apiUrl = it.apiUrl
                )
            }
    }

    override suspend fun insertOrUpdateListData(
        apiUrl: String,
        modelVersion: Int,
        modelType: String,
        content: String
    ) {
        apiDataQueries.insertOrUpdateListData(
            apiUrl,
            modelVersion.toLong(),
            modelType,
            content
        )
    }

    override suspend fun deleteListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String) {
        apiDataQueries.deleteListDataByModelVersionAndApiUrl(modelVersion.toLong(), apiUrl)
    }

    override suspend fun deleteListDataByApiUrl(apiUrl: String) {
        apiDataQueries.deleteListDataByApiUrl(apiUrl)
    }

    override suspend fun getContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>? {
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndModelType(
                modelVersion.toLong(),
                modelType
            ).executeAsList()

        if (contentData.isEmpty()) return null

        return contentData.map {
            ContentData(
                modelId = it.modelId?.toInt(),
                modelType = it.modelType,
                modelVersion = it.modelVersion?.toInt(),
                content = it.content,
                apiUrl = it.apiUrl
            )
        }
    }

    override suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(
        modelVersion: Int,
        modelType: String,
        apiUrl: String
    ): ContentData? {
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndModelTypeAndApiUrl(
                modelVersion.toLong(),
                modelType,
                apiUrl
            ).executeAsOneOrNull() ?: return null

        return ContentData(
                modelId = contentData.modelId?.toInt(),
                modelType = contentData.modelType,
                modelVersion = contentData.modelVersion?.toInt(),
                content = contentData.content,
                apiUrl = contentData.apiUrl
            )
    }

    override suspend fun getContentDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): ContentData? {
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndApiUrl(
                modelVersion.toLong(),
                apiUrl
            ).executeAsOneOrNull()
                ?: return null

        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }

    override suspend fun getContentDataByModelType(modelType: String): List<ContentData>? {
        val contentData = apiDataQueries.getContentDataByModelType(modelType).executeAsList()
        if (contentData.isEmpty()) return null

        return contentData.map {
            ContentData(
                modelId = it.modelId?.toInt(),
                modelType = it.modelType,
                modelVersion = it.modelVersion?.toInt(),
                content = it.content,
                apiUrl = it.apiUrl
            )
        }
    }

    override suspend fun getContentDataByApiUrl(apiUrl: String): ContentData? {
        val contentData = apiDataQueries.getContentDataByApiUrl(apiUrl).executeAsOneOrNull()
            ?: return null

        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }

    override suspend fun insertOrUpdateContentData(
        modelId: Int?,
        modelType: String?,
        modelVersion: Int?,
        content: String?,
        apiUrl: String?
    ) {
        apiDataQueries.insertOrUpdateContentData(
            modelId?.toLong(),
            modelType,
            modelVersion?.toLong(),
            content,
            apiUrl
        )
    }

    override suspend fun deleteContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ) {
        apiDataQueries.deleteContentDataByModelVersionAndModelType(
            modelVersion.toLong(),
            modelType
        )
    }

    override suspend fun deleteContentDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ) {
        apiDataQueries.deleteContentDataByModelVersionAndApiUrl(
            modelVersion.toLong(),
            apiUrl
        )
    }

    override suspend fun deleteContentDataByModelType(modelType: String) {
        apiDataQueries.deleteContentDataByModelType(modelType)
    }

    override suspend fun deleteContentDataByApiUrl(apiUrl: String) {
        apiDataQueries.deleteContentDataByApiUrl(apiUrl)
    }

    override suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String) {
        apiDataQueries.deleteContentDataByModelIdAndModelType(modelId.toLong(), modelType)
    }

    override suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(
        modelType: String,
        modelVersion: Int,
        modelIds: List<Int>
    ): List<ContentData>? {
        val contentData =
            apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
                modelType,
                modelVersion.toLong(),
                modelIds.map { it.toLong() }
            ).executeAsList()
        if (contentData.isEmpty()) return null

        return contentData.map {
            ContentData(
                modelId = it.modelId?.toInt(),
                modelType = it.modelType,
                modelVersion = it.modelVersion?.toInt(),
                content = it.content,
                apiUrl = it.apiUrl
            )
        }
    }
    override suspend fun getContentDataByModelTypeAndModelVersionAndModelId(
        modelType: String,
        modelVersion: Int,
        modelId: Int
    ): ContentData? {
        val contentData = apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelId(
                modelType,
                modelVersion.toLong(),
                modelId.toLong()
            ).executeAsOneOrNull() ?: return null

        return ContentData(
                modelId = contentData.modelId?.toInt(),
                modelType = contentData.modelType,
                modelVersion = contentData.modelVersion?.toInt(),
                content = contentData.content,
                apiUrl = contentData.apiUrl
            )
    }
}