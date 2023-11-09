package com.swensonhe.strapikmm.database.dao

import com.swensonhe.caching.datasource.database.sqldelight.ApiDataQueries
import com.swensonhe.strapikmm.database.ContentData

/**
 * An implementation of the [ApiDataDao] interface for managing API data using SQL queries.
 *
 * @param apiDataQueries An instance of [ApiDataQueries] providing SQL query functionality.
 */
class ApiDataDaoImplementation(private val apiDataQueries: ApiDataQueries) : ApiDataDao {

    override suspend fun getListDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): List<ContentData> {
        // Query the database to get a ListData object with the given model version and API URL.
        val listData =
            apiDataQueries.getListDataByModelVersionAndApiUrl(modelVersion.toLong(), apiUrl)
                .executeAsOneOrNull() ?: return emptyList()

        // Extract content IDs from the ListData and split them into a list of Long values.
        val contentIds = listData.content.split(",").map { it.trim().toLong() }

        // Query the database to get ContentData objects based on model type, model version, and content IDs.
        return apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
            listData.modelType,
            modelVersion.toLong(),
            contentIds
        )
            .executeAsList()
            .map {
                // Map the results to ContentData objects.
                ContentData(
                    modelId = it.modelId?.toInt(),
                    modelType = it.modelType,
                    modelVersion = it.modelVersion?.toInt(),
                    content = it.content,
                    apiUrl = it.apiUrl
                )
            }
    }


    /**
     * Retrieves a list of [ContentData] based on the specified model version and model type.
     *
     * @param modelVersion The version of the data model to filter by.
     * @param modelType The type of the data model to filter by.
     * @return A list of [ContentData] matching the given model version and model type, or null if no data is found.
     */
    override suspend fun getListDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>? {
        // Get the list data based on model version and model type from the database, If no list data is found, return null
        val listData =
            apiDataQueries.getListDataByModelVersionAndModelType(modelVersion.toLong(), modelType)
                .executeAsOneOrNull() ?: return null

        // Extract content IDs from the list data.
        val contentIds = listData.content.split(",").map { it.toLong() }

        // Retrieve content data based on the list data information.
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

    /**
     * Retrieves a list of [ContentData] based on the specified API URL.
     *
     * @param apiUrl The URL of the API to filter by.
     * @return A list of [ContentData] matching the given API URL, or null if no data is found.
     */
    override suspend fun getListDataByApiUrl(apiUrl: String): List<ContentData>? {
        // Get the list data based on the API URL from the database, If no list data is found, return null
        val listData =
            apiDataQueries.getListDataByApiUrl(apiUrl).executeAsOneOrNull() ?: return null

        // Extract content IDs from the list data.
        val contentIds = listData.content.split(",").map { it.toLong() }

        // Retrieve content data based on the list data information.
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

    /**
     * Retrieves a list of [ContentData] based on the specified model type.
     *
     * @param modelType The type of the data model to filter by.
     * @return A list of [ContentData] matching the given model type, or null if no data is found.
     */
    override suspend fun getListDataByModelType(modelType: String): List<ContentData>? {
        // Get a list of list data entries based on the model type from the database.
        val listData = apiDataQueries.getListDataByModelType(modelType).executeAsList()

        // If no list data entries are found, return null.
        if (listData.isEmpty()) return null

        // Extract content IDs from all list data entries and flatten the list.
        val contentIds = listData.map { it.content.split(",").map { it.toLong() } }.flatten()

        // Retrieve content data based on the list data information.
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

    /**
     * Inserts or updates list data in the database for a specific API URL, model version, and model type.
     *
     * @param apiUrl The URL of the API.
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     * @param content The content data to insert or update.
     */
    override suspend fun insertOrUpdateListData(
        apiUrl: String,
        modelVersion: Int,
        modelType: String,
        content: String
    ) {
        // Insert or update list data in the database.
        apiDataQueries.insertOrUpdateListData(
            apiUrl,
            modelVersion.toLong(),
            modelType,
            content
        )
    }

    /**
     * Deletes list data based on the model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     */
    override suspend fun deleteListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String) {
        // Delete list data from the database based on the model version and API URL.
        apiDataQueries.deleteListDataByModelVersionAndApiUrl(modelVersion.toLong(), apiUrl)
    }

    /**
     * Deletes list data based on the API URL.
     *
     * @param apiUrl The URL of the API.
     */
    override suspend fun deleteListDataByApiUrl(apiUrl: String) {
        // Delete list data from the database based on the API URL.
        apiDataQueries.deleteListDataByApiUrl(apiUrl)
    }

    /**
     * Deletes list data based on the model type.
     *
     * @param modelType The type of the data model.
     */
    override suspend fun getContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>? {
        // Get a list of content data entries based on the model version and model type from the database.
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndModelType(
                modelVersion.toLong(),
                modelType
            ).executeAsList()

        // If no content data entries are found, return null.
        if (contentData.isEmpty()) return null

        // Map the results to ContentData objects.
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

    /**
     * Retrieves [ContentData] based on model version, model type, and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    override suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(
        modelVersion: Int,
        modelType: String,
        apiUrl: String
    ): ContentData? {
        // Get content data based on model version, model type, and API URL from the database, If no content data is found, return null
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndModelTypeAndApiUrl(
                modelVersion.toLong(),
                modelType,
                apiUrl
            ).executeAsOneOrNull() ?: return null

        // Map the results to a ContentData object.
        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }

    /**
     * Retrieves [ContentData] based on model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    override suspend fun getContentDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): ContentData? {
        // Get content data based on model version and API URL from the database, If no content data is found, return null
        val contentData =
            apiDataQueries.getContentDataByModelVersionAndApiUrl(
                modelVersion.toLong(),
                apiUrl
            ).executeAsOneOrNull()
                ?: return null

        // Map the results to a ContentData object.
        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }

    /**
     * Retrieves a list of [ContentData] based on the specified model type.
     *
     * @param modelType The type of the data model to filter by.
     * @return A list of [ContentData] matching the given model type, or null if no data is found.
     */
    override suspend fun getContentDataByModelType(modelType: String): List<ContentData>? {
        // Get a list of content data entries based on the model type from the database.
        val contentData = apiDataQueries.getContentDataByModelType(modelType).executeAsList()
        // If no content data entries are found, return null.
        if (contentData.isEmpty()) return null

        // Map the results to ContentData objects.
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

    /**
     * Retrieves [ContentData] based on the API URL.
     *
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    override suspend fun getContentDataByApiUrl(apiUrl: String): ContentData? {
        // Get content data based on the API URL from the database, If no content data is found, return null
        val contentData = apiDataQueries.getContentDataByApiUrl(apiUrl).executeAsOneOrNull()
            ?: return null

        // Map the results to a ContentData object.
        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }

    /**
     * Inserts or updates [ContentData].
     *
     * @param modelId The identifier of the data model.
     * @param modelType The type of the data model.
     * @param modelVersion The version of the data model.
     * @param content The content data to insert or update.
     * @param apiUrl The URL of the API.
     */
    override suspend fun insertOrUpdateContentData(
        modelId: Int?,
        modelType: String?,
        modelVersion: Int?,
        content: String?,
        apiUrl: String?
    ) {
        // Insert or update content data in the database.
        apiDataQueries.insertOrUpdateContentData(
            modelId?.toLong(),
            modelType,
            modelVersion?.toLong(),
            content,
            apiUrl
        )
    }

    /**
     * Deletes content data based on the model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     */
    override suspend fun deleteContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ) {
        // Delete content data from the database based on the model version and model type.
        apiDataQueries.deleteContentDataByModelVersionAndModelType(
            modelVersion.toLong(),
            modelType
        )
    }

    /**
     * Deletes content data based on the API URL.
     *
     * @param apiUrl The URL of the API.
     */
    override suspend fun deleteContentDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ) {
        // Delete content data from the database based on the model version and API URL.
        apiDataQueries.deleteContentDataByModelVersionAndApiUrl(
            modelVersion.toLong(),
            apiUrl
        )
    }

    /**
     * Deletes content data based on the model type.
     *
     * @param modelType The type of the data model.
     */
    override suspend fun deleteContentDataByModelType(modelType: String) {
        // Delete content data from the database based on the model type.
        apiDataQueries.deleteContentDataByModelType(modelType)
    }

    /**
     * Deletes content data based on the API URL.
     *
     * @param apiUrl The URL of the API.
     */
    override suspend fun deleteContentDataByApiUrl(apiUrl: String) {
        // Delete content data from the database based on the API URL.
        apiDataQueries.deleteContentDataByApiUrl(apiUrl)
    }

    /**
     * Deletes content data based on the model ID and model type.
     *
     * @param modelId The identifier of the data model.
     * @param modelType The type of the data model.
     */
    override suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String) {
        // Delete content data from the database based on the model ID and model type.
        apiDataQueries.deleteContentDataByModelIdAndModelType(modelId.toLong(), modelType)
    }

    /**
     * Retrieves a list of [ContentData] based on the specified model type, model version, and content IDs.
     *
     * @param modelType The type of the data model to filter by.
     * @param modelVersion The version of the data model to filter by.
     * @param modelIds The IDs of the data model to filter by.
     * @return A list of [ContentData] matching the given model type, model version, and content IDs, or null if no data is found.
     */
    override suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(
        modelType: String,
        modelVersion: Int,
        modelIds: List<Int>
    ): List<ContentData>? {
        // Get a list of content data entries based on the model type, model version, and content IDs from the database.
        val contentData =
            apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelIds(
                modelType,
                modelVersion.toLong(),
                modelIds.map { it.toLong() }
            ).executeAsList()
        // If no content data entries are found, return null.
        if (contentData.isEmpty()) return null

        // Map the results to ContentData objects.
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

    /**
     * Retrieves [ContentData] based on the specified model type, model version, and model ID.
     *
     * @param modelType The type of the data model to filter by.
     * @param modelVersion The version of the data model to filter by.
     * @param modelId The ID of the data model to filter by.
     * @return [ContentData] matching the given model type, model version, and model ID, or null if no data is found.
     */
    override suspend fun getContentDataByModelTypeAndModelVersionAndModelId(
        modelType: String,
        modelVersion: Int,
        modelId: Int
    ): ContentData? {
        // Get content data based on model type, model version, and model ID from the database, If no content data is found, return null
        val contentData = apiDataQueries.getContentDataByModelTypeAndModelVersionAndModelId(
            modelType,
            modelVersion.toLong(),
            modelId.toLong()
        ).executeAsOneOrNull() ?: return null

        // Map the results to a ContentData object.
        return ContentData(
            modelId = contentData.modelId?.toInt(),
            modelType = contentData.modelType,
            modelVersion = contentData.modelVersion?.toInt(),
            content = contentData.content,
            apiUrl = contentData.apiUrl
        )
    }
}