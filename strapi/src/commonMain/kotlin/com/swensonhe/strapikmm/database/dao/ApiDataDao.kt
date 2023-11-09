package com.swensonhe.strapikmm.database.dao

import com.swensonhe.strapikmm.database.ContentData

/**
 * An interface representing a Data Access Object (DAO) for accessing and managing API data.
 * Implementations of this interface provide methods to retrieve, insert, update, and delete data
 * related to various models and APIs.
 */
interface ApiDataDao {

    /**
     * Retrieves a list of [ContentData] based on model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     * @return A list of [ContentData] or null if no data is found.
     */
    suspend fun getListDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): List<ContentData>?

    /**
     * Retrieves a list of [ContentData] based on model version and model type.
     *
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     * @return A list of [ContentData] or null if no data is found.
     */
    suspend fun getListDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>?

    /**
     * Retrieves a list of [ContentData] based on the API URL.
     *
     * @param apiUrl The URL of the API.
     * @return A list of [ContentData].
     */
    suspend fun getListDataByApiUrl(apiUrl: String): List<ContentData>?

    /**
     * Retrieves a list of [ContentData] based on the model type.
     *
     * @param modelType The type of the data model.
     * @return A list of [ContentData].
     */
    suspend fun getListDataByModelType(modelType: String): List<ContentData>?

    /**
     * Inserts or updates a list of content data.
     *
     * @param apiUrl The URL of the API.
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model (e.g., "contact").
     * @param content The content data to insert or update (JSON string).
     */
    suspend fun insertOrUpdateListData(
        apiUrl: String,
        modelVersion: Int,
        modelType: String,
        content: String
    )

    /**
     * Deletes list data based on model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     */
    suspend fun deleteListDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String)

    /**
     * Deletes list data based on the API URL.
     *
     * @param apiUrl The URL of the API.
     */
    suspend fun deleteListDataByApiUrl(apiUrl: String)

    /**
     * Retrieves a list of [ContentData] based on model version and model type.
     *
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     * @return A list of [ContentData] or null if no data is found.
     */
    suspend fun getContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    ): List<ContentData>?

    /**
     * Retrieves [ContentData] based on model version, model type, and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    suspend fun getContentDataByModelVersionAndModelTypeAndApiUrl(
        modelVersion: Int,
        modelType: String,
        apiUrl: String
    ): ContentData?

    /**
     * Retrieves [ContentData] based on model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    suspend fun getContentDataByModelVersionAndApiUrl(
        modelVersion: Int,
        apiUrl: String
    ): ContentData?

    /**
     * Retrieves a list of [ContentData] based on the model type.
     *
     * @param modelType The type of the data model.
     * @return A list of [ContentData] or null if no data is found.
     */
    suspend fun getContentDataByModelType(modelType: String): List<ContentData>?

    /**
     * Retrieves [ContentData] based on the API URL.
     *
     * @param apiUrl The URL of the API.
     * @return [ContentData] matching the criteria.
     */
    suspend fun getContentDataByApiUrl(apiUrl: String): ContentData?

    /**
     * Inserts or updates [ContentData].
     *
     * @param modelId The identifier of the data model.
     * @param modelType The type of the data model.
     * @param modelVersion The version of the data model.
     * @param content The content data to insert or update.
     * @param apiUrl The URL of the API.
     */
    suspend fun insertOrUpdateContentData(
        modelId: Int?,
        modelType: String?,
        modelVersion: Int?,
        content: String?,
        apiUrl: String?
    )

    /**
     * Deletes [ContentData] based on model version and model type.
     *
     * @param modelVersion The version of the data model.
     * @param modelType The type of the data model.
     */
    suspend fun deleteContentDataByModelVersionAndModelType(
        modelVersion: Int,
        modelType: String
    )

    /**
     * Deletes [ContentData] based on model version and API URL.
     *
     * @param modelVersion The version of the data model.
     * @param apiUrl The URL of the API.
     */
    suspend fun deleteContentDataByModelVersionAndApiUrl(modelVersion: Int, apiUrl: String)

    /**
     * Deletes [ContentData] based on the model type.
     *
     * @param modelType The type of the data model.
     */
    suspend fun deleteContentDataByModelType(modelType: String)

    /**
     * Deletes [ContentData] based on the API URL.
     *
     * @param apiUrl The URL of the API.
     */
    suspend fun deleteContentDataByApiUrl(apiUrl: String)

    /**
     * Deletes [ContentData] based on model ID and model type.
     *
     * @param modelId The identifier of the data model.
     * @param modelType The type of the data model.
     */
    suspend fun deleteContentDataByModelIdAndModelType(modelId: Int, modelType: String)

    /**
     * Retrieves a list of [ContentData] based on model type, model version, and a list of model IDs.
     *
     * @param modelType The type of the data model.
     * @param modelVersion The version of the data model.
     * @param modelIds A list of model identifiers.
     * @return A list of [ContentData] or null if no data is found.
     */
    suspend fun getContentDataByModelTypeAndModelVersionAndModelIds(
        modelType: String,
        modelVersion: Int,
        modelIds: List<Int>
    ): List<ContentData>?

    /**
     * Retrieves [ContentData] based on model type, model version, and a model ID.
     *
     * @param modelType The type of the data model.
     * @param modelVersion The version of the data model.
     * @param modelId The identifier of the data model.
     * @return [ContentData] matching the criteria.
     */
    suspend fun getContentDataByModelTypeAndModelVersionAndModelId(
        modelType: String,
        modelVersion: Int,
        modelId: Int
    ): ContentData?
}