package com.swensonhe.strapikmm.database

/**
 * Represents data associated with a specific content model, including its identifier,
 * type, version, content payload, and associated API URL.
 *
 * @param modelId The unique identifier for the content model, or null if not applicable.
 * @param modelType The type of the content model (e.g., "contact"), or null if not applicable.
 * @param modelVersion The version of the content model, or null if not applicable.
 * @param content The content data associated with the model (JSON string), or null if not existing.
 * @param apiUrl The URL of the API that provides this content data, or null if not applicable.
 */
data class ContentData(
    val modelId: Int?,
    val modelType: String?,
    val modelVersion: Int?,
    val content: String?,
    val apiUrl: String?
)
