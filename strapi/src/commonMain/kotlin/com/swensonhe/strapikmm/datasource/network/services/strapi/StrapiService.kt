@file:OptIn(ExperimentalSerializationApi::class)

package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.database.LocalDataRepository
import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import com.swensonhe.strapikmm.datasource.network.StrapiRequestBuilder
import com.swensonhe.strapikmm.model.DataWrapper
import com.swensonhe.strapikmm.model.MetaResponse
import com.swensonhe.strapikmm.model.Paging
import com.swensonhe.strapikmm.model.PagingResponse
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.annotations.getModelVersion
import com.swensonhe.strapikmm.database.DatabaseDriverFactory
import com.swensonhe.strapikmm.util.nullIfEmpty
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

/**
 * A service class that provides functions for performing HTTP requests and parsing the responses.
 *
 * @param httpClient An instance of [HttpClient] to use for sending HTTP requests.
 * @param baseUrl The base URL to use for all requests.
 * @param kmmPreference An instance of [KmmPreference] to use for storing and retrieving data.
 * @param context An optional context object to use for accessing platform-specific functionality.
 *
 * @see HttpClient
 * @see KmmPreference
 */
class StrapiService(
    val httpClient: HttpClient,
    baseUrl: String,
    kmmPreference: KmmPreference,
    context: Any? = null,
) : KmmBaseService(baseUrl, kmmPreference) {

    val localDataRepository by lazy {
        LocalDataRepository(DatabaseDriverFactory(context))
    }

    /**
     * Perform an HTTP GET request and parse the response into an object of type [T].
     * This function is intended for use with the Strapi APIs.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return An object of type [T] representing the parsed response.
     *
     * @throws Throwable if any errors occur during the request or response parsing.
     *
     * Usage Example:
     * ```kotlin
     * val user: User = get {
     *     endpoint("/users/123")
     *     strapiQueryBuilder {
     *      populate("posts")
     *     }
     *     // Add headers or other request customizations if needed
     * }
     * ```
     *
     * In this example, an HTTP GET request is made to retrieve a user's data, and the response is parsed into a `User` object.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see HttpMethod
     * @see JsonElement
     * @see JsonFlatter
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> get(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()

        // Execute the provided lambda to configure the request
        builder.requestBuilder()

        // Build the HTTP request using the request builder
        val request = buildRequest(builder, HttpMethod.Get)

        // Send the HTTP request and get the response as a JSON element
        val json = httpClient.get(request).body<JsonElement>()

        // Flatten the JSON response and convert it to the specified type [T]
        return JsonFlatter.flat<T>(json).convert<T>()
    }

    /**
     * Create a Flow of data by performing an HTTP GET request and parsing the response into a stream of objects of type [T].
     * This function is designed for use with the Strapi API and returns a Flow for reactive programming.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return A Flow of objects of type [T] representing the parsed response.
     *
     * @throws Throwable if any errors occur during the request or response parsing.
     *
     * Usage Example:
     * ```kotlin
     * val usersFlow: Flow<User> = getFlow {
     *     endpoint("/users")
     *     strapiQueryBuilder {
     *        populate("posts")
     *     }
     *     // Add headers or other request customizations if needed
     * }
     * ```
     *
     * In this example, an HTTP GET request is made to retrieve a list of users, and the response is provided as a Flow of `User` objects.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. If [T] is a [PagingResponse] or [DataWrapper],
     * the appropriate specialized function is called. Otherwise, the [getDefault] function is used to perform the request.
     *
     * @see StrapiRequestBuilder
     * @see getPaged
     * @see getOne
     * @see getDefault
     * @see Flow
     */
    @Throws(Throwable::class)
    inline fun <reified T> getFlow(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): Flow<T> {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()

        // Execute the provided lambda to configure the request
        builder.requestBuilder()

        // Check the type [T] and select the appropriate function based on the type
        return if (T::class == PagingResponse::class) {
            // If [T] is a PagingResponse, use the getPaged function
            getPaged(requestBuilder)
        } else if (T::class == DataWrapper::class) {
            // If [T] is a DataWrapper, use the getOne function
            getOne(requestBuilder)
        } else {
            // For other types, use the getDefault function
            getDefault(requestBuilder)
        }
    }

    /**
     * Perform an HTTP GET request to fetch data of type [T] with caching support using the default fetch strategy.
     * This function is designed for use with the Strapi API and returns a Flow for reactive programming.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return A Flow of objects of type [T] representing the parsed response with caching support.
     *
     * @throws Throwable if any errors occur during the request, response parsing, or cache operations.
     *
     * Usage Example:
     * ```kotlin
     * val usersFlow: Flow<User> = getDefault {
     *     endpoint("/users")
     *     strapiQueryBuilder {
     *        populate("posts")
     *     }
     *     // Add headers or other request customizations if needed
     * }
     * ```
     *
     * In this example, an HTTP GET request is made to retrieve a list of users with the default fetch strategy.
     * The response is provided as a Flow of `User` objects with caching support.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. Caching is performed
     * based on the fetch strategy specified in the request builder.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see FetchStrategy
     * @see Flow
     * @see JsonWithIgnoredUnknownKeys
     * @see JsonFlatter
     */
    @Throws(Throwable::class)
    inline fun <reified T> getDefault(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}
    ): Flow<T> = flow {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()

        // Execute the provided lambda to configure the request
        builder.requestBuilder()

        // Build the HTTP request to extract the path, URL, and fetch strategy
        val request = buildRequest(builder, HttpMethod.Get)

        // Get the url path from the request
        val apiPath = request.url.encodedPath

        // Get the fetch strategy from the request
        val fetchStrategy = builder.requestFetchStrategy

        // Get the class name of the request type [T] if available (used for caching) or use simple name of [T] as a fallback (e.g., "User")
        val requestClassName = builder.requestClassName ?: T::class.simpleName ?: ""

        // Get the model version from the request type [T] if available (used for caching) or use 1 as a fallback
        val modelVersion = serializer<T>().getModelVersion()

        // Get data from cache if the fetch strategy is CACHE_THEN_REMOTE
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
            // If the fetch strategy is CACHE_THEN_REMOTE, get the cached data from the local database
            val localData = if (requestClassName.isNotEmpty()) {
                // If the request class name is available, get the cached data by model version and model type
                localDataRepository.getContentDataByModelVersionAndModelTypeAndApiUrl(
                    modelVersion,
                    requestClassName,
                    apiPath
                )
            } else {
                // If the request class name is not available, get the cached data by API URL
                localDataRepository.getContentDataByApiUrl(apiPath)
            }

            if (localData?.content.isNullOrEmpty().not()) {
                // If the cached data is available, emit the cached data after parsing it into an object of type [T]
                val json = JsonWithIgnoredUnknownKeys.parseToJsonElement(localData?.content!!)
                // emit the cached data
                emit(JsonFlatter.flat<T>(json).convert<T>())
            }
        }

        // Send the HTTP request and get the response as a JSON element
        val json = httpClient.get(request).body<JsonElement>()
        // Flatten the JSON response and convert it to the specified type [T]
        val response = JsonFlatter.flat<T>(json).convert<T>()

        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
            // If the fetch strategy is CACHE_THEN_REMOTE, cache the response data
            // Convert the response to a JSON element to be able to cache it in the local database
            localDataRepository.insertOrUpdateContentData(
                modelVersion,
                requestClassName,
                Json.encodeToString(json),
                apiPath,
                null
            )
        }

        // then emit the response data to the flow
        emit(response)
    }

    /**
     *  **DO NOT USE THIS METHOD DIRECTLY, USE [getFlow] INSTEAD**
     *
     * Perform an HTTP GET request to fetch paged data of type [T] with caching support.
     * This function is designed for use with the Strapi API and returns a Flow for reactive programming.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return A Flow of objects of type [T] representing the parsed paged response with caching support.
     *
     * @throws Throwable if any errors occur during the request, response parsing, or cache operations.
     *
     * Usage Example:
     * ```kotlin
     * val usersFlow: Flow<PagingResponse<User>> = getPaged {
     *     path = "/users"
     *     // Add headers or other request customizations if needed
     * }
     * ```
     *
     * In this example, an HTTP GET request is made to retrieve a paged list of users with caching support.
     * The response is provided as a Flow of `PagingResponse<User>` with caching support.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the paged response.
     * Caching is performed based on the fetch strategy specified in the request builder.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see FetchStrategy
     * @see Flow
     * @see PagingResponse
     * @see MetaResponse
     */
    @Throws(Throwable::class)
    inline fun <reified T> getPaged(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}
    ): Flow<T> = flow {

        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()

        // Execute the provided lambda to configure the request
        builder.requestBuilder()

        // Get the model serializer for response parsing, or throw an exception if not provided
        // This is required for parsing the response into an object of type [T] and for caching
        // without the serializer, we can't get the model version and the model class name and the type to flat based on it
        val modelSerializer = builder.modelSerializer
            ?: throw Throwable("You must provide the responseType in the requestBuilder")

        // Get the model version for serialization
        val modelVersion = modelSerializer.getModelVersion()

        // Get the page number from the query builder or use 1 as the default
        val page = builder.queryBuilder?.pagingData?.page ?: 1

        // Get the fetch strategy from the request builder
        val fetchStrategy = builder.requestFetchStrategy

        // Build the HTTP request to extract the URL, class name, and API path
        val request = buildRequest(builder, HttpMethod.Get)

        // Get the class name of the request type [T] if available (used for caching) or use simple name of [T] as a fallback (e.g., "User")
        val requestClassName = builder.requestClassName ?: ""

        // Get the API URL from the request
        val apiUrl = request.url.buildString()

        // Get the API path from the request URL (e.g., "/users") to be used for caching
        val apiPath = request.url.encodedPath

        // Get data from cache if the fetch strategy is CACHE_THEN_REMOTE and it's the first page
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE && page == 1) {
            // If the fetch strategy is CACHE_THEN_REMOTE and it's the first page, get the cached data from the local database by model version and API URL
            val localData =
                localDataRepository.getListDataByModelVersionAndApiUrl(modelVersion, apiUrl)
            // Parse the cached data into a list of objects of type [T]
            val listItems = localData.orEmpty().map { localItem ->
                // Parse the cached data into a list of objects of type [T]
                if (localItem.content.isNullOrEmpty().not()) {
                    // parse the data based on the model serializer provided
                    Json.decodeFromJsonElement(
                        modelSerializer,
                        JsonWithIgnoredUnknownKeys.parseToJsonElement(localItem.content!!)
                    )
                } else {
                    // if the content is null or empty then return
                    null
                }
                // filter out null items if any
            }.filterNotNull()

            if (listItems.isNotEmpty()) {
                // If the cached data is available, emit the cached data after parsing it into an object of type [T]
                emit(
                    PagingResponse(
                        data = listItems,
                        meta = MetaResponse(
                            Paging(
                                page = 1,
                                pageSize = listItems.size,
                                total = listItems.size,
                                pageCount = 1
                            )
                        )
                    ) as T
                )
            }
        }

        // Send the HTTP request and get the response as a JSON element
        val json = httpClient.get(request).body<JsonElement>()
        // Flatten the JSON response and convert it to the specified type [T]
        val flatResponse = JsonFlatter.flat<T>(json)
        // Convert the flat response to an object of type [T]
        val response = flatResponse.convert<T>()

        // Cache the whole list and each item individually if it's the first page or the cache strategy is CACHE_LAST
        if (page == 1) {
            // Converting the list to json array to be able to cache it
            val jsonArray = flatResponse.jsonObject["data"]?.jsonArray.orEmpty()
            // get the ids of the elements in the list to be able to cache them individually based on their ids
            val elementsIds = jsonArray.mapNotNull { jsonElement ->
                jsonElement.jsonObject["id"]?.jsonPrimitive?.content
            }.joinToString(",")

            // Cache each item in the list individually so that we can update the cache when the item is updated
            jsonArray.forEach { jsonElement ->
                // convert the item to json string to be able to cache it
                val jsonContent = Json.encodeToString(jsonElement)

                // get the id of the item to be able to cache it individually
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()

                // if the id is not null and the class name is not empty then cache the item individually
                if (id != null && requestClassName.isNotEmpty()) {
                    // get the cached item from the local database
                    val localCachedItem =
                        localDataRepository.getContentDataByModelTypeAndModelVersionAndModelId(
                            requestClassName,
                            modelVersion,
                            id
                        )

                    // if the cached item is null or empty then insert it in the database
                    if (localCachedItem?.content.isNullOrEmpty()) {
                        localDataRepository.insertOrUpdateContentData(
                            modelVersion,
                            requestClassName,
                            jsonContent,
                            "$apiPath/$id",
                            id
                        )
                    } else {
                        // if the cached item is not null or empty then update it in the database
                        // merge the new data with the cached data
                        val itemJsonElement =
                            JsonWithIgnoredUnknownKeys.parseToJsonElement(localCachedItem?.content!!)
                        // merge the new data with the cached data if the new data is a json object
                        if (itemJsonElement as? JsonObject != null && jsonElement as? JsonObject != null) {
                            // convert the cached item to mutable map to be able to merge the new data with it
                            val cachedJsonObject = itemJsonElement.toMutableMap()
                            // convert the new data to mutable map to be able to merge it with the cached data
                            val newJsonObject = jsonElement.toMutableMap()
                            // merge the new data with the cached data
                            cachedJsonObject.putAll(newJsonObject)
                            // convert the merged data to json string to be able to cache it
                            val jsonData = JsonObject(cachedJsonObject).toString()

                            // update the cached item in the database
                            localDataRepository.insertOrUpdateContentData(
                                modelVersion,
                                requestClassName,
                                jsonData,
                                "$apiPath/$id",
                                id
                            )
                        }
                    }
                }
            }

            // convert the list to json string to be able to cache it in the database as a list of items of type [T]
            localDataRepository.insertOrUpdateListData(
                apiUrl,
                modelVersion,
                requestClassName,
                elementsIds
            )
        }

        // then emit the response data to the flow
        emit(response)
    }

    /**
     *  DO NOT USE THIS METHOD DIRECTLY, USE [getFlow] INSTEAD**
     *
     * Retrieve a single item of type [T] using an HTTP GET request and a provided request builder, while optionally
     * handling cache updates.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return A [Flow] of type [T] representing the retrieved item.
     *
     * @throws Throwable if any errors occur during the request, response processing, or cache updates.
     *
     * Usage Example:
     * ```kotlin
     * val itemFlow: Flow<Item> = getOne {
     *     endpoint("/items/1")
     *     // Customize the request, add headers or other configuration as needed
     * }
     * ```
     *
     * In this example, an HTTP GET request is made to retrieve a single item, and the response is processed. Cache updates
     * are handled when the response indicates an item update.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. The function provides
     * flexibility to customize the GET request, process the response, and optionally handle cache updates. It can throw a `Throwable`
     * in case of errors during the request, response processing, or cache updates.
     *
     * @see StrapiRequestBuilder
     * @see HttpMethod
     * @see FetchStrategy
     * @see JsonElement
     * @see DataWrapper
     * @see localDataRepository
     */
    @Throws(Throwable::class)
    inline fun <reified T> getOne(crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}): Flow<T> =
        flow {

            // Create a new StrapiRequestBuilder
            val builder = StrapiRequestBuilder()
            // Execute the provided lambda to configure the request
            builder.requestBuilder()

            // Get the model serializer for response parsing, or throw an exception if not provided
            val modelSerializer = builder.modelSerializer
                ?: throw Throwable("You must provide the responseType in the requestBuilder")

            // Get the model version for serialization and caching purposes from the model serializer
            val modelVersion = modelSerializer.getModelVersion()

            // Get the class name of the request type [T] if available (used for caching) or use simple name of [T] as a fallback (e.g., "User")
            val requestClassName = builder.requestClassName ?: ""

            // Get the fetch strategy from the request builder
            val fetchStrategy = builder.requestFetchStrategy

            // Build the HTTP request using the request builder
            val request = buildRequest(builder, HttpMethod.Get)

            // Get the API path from the request URL (e.g., "/users") to be used for caching
            val apiPath = request.url.encodedPath

            // We need to get the id from the encoded path which is the last part of the path and it can be like posts/1 or comments/1 .. etc
            // so we need to get the last part of the path and check if it's a number then it's the id
            val entityId = apiPath.split("/").lastOrNull()?.toInt()

            // get data from cache if the fetch strategy is CACHE_THEN_REMOTE
            if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
                val localData = if (entityId != null && requestClassName.isNotEmpty()) {
                    // get the cached data by model version and model type and model id if available
                    localDataRepository.getContentDataByModelTypeAndModelVersionAndModelId(
                        requestClassName,
                        modelVersion,
                        entityId
                    )
                    // if the class name is not available then get the cached data by model version and api url
                } else if (entityId == null && requestClassName.isEmpty()) {
                    // get the cached data by model version and api url
                    localDataRepository.getContentDataByModelVersionAndApiUrl(modelVersion, apiPath)
                } else {
                    // if the class name is available then get the cached data by api url
                    localDataRepository.getContentDataByApiUrl(apiPath)
                }

                // if the cached data is not null or empty then emit it
                if (localData?.content.isNullOrEmpty().not()) {
                    // parse the cached data based on the model serializer provided
                    val cachedData = Json.decodeFromString(modelSerializer, localData?.content!!)
                    // emit the cached data to the flow
                    emit(DataWrapper(cachedData) as T)
                }
            }

            // Send the HTTP request and get the response as a JSON element
            val json = httpClient.get(request).body<JsonElement>()
            // Flatten the JSON response and convert it to the specified type [T]
            val response = JsonFlatter.flat<T>(json).convert<T>()

            // convert the response to json element to be able to cache it
            val responseJson = Json.encodeToJsonElement(serializer<T>(), response)
            // get the data from the response if available
            val jsonObject = responseJson.jsonObject["data"]?.jsonObject.orEmpty()

            // convert the data to json string to be able to cache it
            val jsonContent = Json.encodeToString(jsonObject)

            // insert or update the cached data in the database based on the fetch strategy
            localDataRepository.insertOrUpdateContentData(
                modelVersion = modelVersion,
                modelType = requestClassName.nullIfEmpty(),
                jsonContent,
                apiPath,
                entityId
            )

            // then emit the response data to the flow
            emit(response)
        }

    /**
     * Perform an HTTP POST request and process the response of type [T].
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return An object of type [T] representing the parsed response from the POST request.
     *
     * @throws Throwable if any errors occur during the request or response processing.
     *
     * Usage Example:
     * ```kotlin
     * val createdItem: Item = post {
     *     endpoint("/items")
     *     body(ItemObject())
     *     // Customize the request, add request body or headers as needed
     * }
     * ```
     *
     * In this example, an HTTP POST request is made to create a new item, and the response is processed.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. The function provides
     * flexibility to customize the POST request and process the response. It can throw a `Throwable` in case of errors during
     * the request or response processing.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see HttpMethod
     * @see JsonElement
     * @see JsonFlatter
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> post(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()
        // Execute the provided lambda to configure the request
        builder.requestBuilder()
        // Send the HTTP POST request and get the response as a JSON element
        val json = httpClient.post(buildRequest(builder, HttpMethod.Post)).body<JsonElement>()

        // Process the response based on the type [T]
        return if (T::class.simpleName == Unit::class.simpleName) {
            // If the response type is Unit, return Unit
            Unit as T
        } else {
            // Otherwise, flatten the JSON response and convert it to an object of type [T]
            JsonFlatter.flat<T>(json).convert()
        }
    }

    /**
     * Perform an HTTP PATCH request and process the response of type [T], while optionally handling cache updates.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return An object of type [T] representing the parsed response from the PATCH request.
     *
     * @throws Throwable if any errors occur during the request, response processing, or cache updates.
     *
     * Usage Example:
     * ```kotlin
     * val updatedUser: User = patch {
     *     endpoint("/users/123")
     *     body(UserObject())
     *     // Add request body or headers if needed
     * }
     * ```
     *
     * In this example, an HTTP PATCH request is made to update a user, and the response is processed. Cache updates are handled
     * when the response indicates an item update.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. The function provides
     * flexibility to customize the PATCH request, process the response, and optionally handle cache updates. It can throw a `Throwable`
     * in case of errors during the request, response processing, or cache updates.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see HttpMethod
     * @see JsonElement
     * @see handleUpdateItem
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> patch(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()
        // Execute the provided lambda to configure the request
        builder.requestBuilder()
        // Build the HTTP request using the request builder
        val request = buildRequest(builder, HttpMethod.Patch)

        // Send the HTTP request and get the response as a JSON element
        val json = httpClient.patch(request).body<JsonElement>()

        return if (T::class.simpleName == Unit::class.simpleName) {
            // If the response type is Unit, return Unit
            Unit as T
        } else {
            // Otherwise, process the response and optionally handle cache updates using the handleUpdateItem function
            handleUpdateItem(json, request, builder)
        }
    }

    /**
     * Perform an HTTP PUT request and process the response of type [T], while optionally handling cache updates.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return An object of type [T] representing the parsed response from the PUT request.
     *
     * @throws Throwable if any errors occur during the request, response processing, or cache updates.
     *
     * Usage Example:
     * ```kotlin
     * val updatedUser: User = put {
     *     endpoint("/users/123")
     *     body(UserObject())
     *     // Add request body or headers if needed
     * }
     * ```
     *
     * In this example, an HTTP PUT request is made to update a user, and the response is processed. Cache updates are handled
     * when the response indicates an item update.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. The function provides
     * flexibility to customize the PUT request, process the response, and optionally handle cache updates. It can throw a `Throwable`
     * in case of errors during the request, response processing, or cache updates.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see HttpMethod
     * @see JsonElement
     * @see handleUpdateItem
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> put(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()
        // Execute the provided lambda to configure the request
        builder.requestBuilder()
        // Build the HTTP request using the request builder
        val request = buildRequest(builder, HttpMethod.Put)

        // Send the HTTP request and get the response as a JSON element
        val json = httpClient.put(request).body<JsonElement>()

        return if (T::class.simpleName == Unit::class.simpleName) {
            // If the response type is Unit, return Unit
            Unit as T
        } else {
            // Otherwise, process the response and optionally handle cache updates
            handleUpdateItem(json, request, builder)
        }
    }

    /**
     * **DO NOT USE THIS METHOD DIRECTLY, USE [PUT, PATCH] INSTEAD**
     *
     * This method is used to update the cache when an item is updated. It processes the JSON response from the API,
     * the original HTTP request, and the request builder to update the cached data.
     *
     * @param json The JSON response from the API.
     * @param request The HTTP request that was sent to the API.
     * @param builder The request builder used to customize the request.
     * @return An object of type [T] representing the parsed response from the API.
     *
     * @throws Throwable if any errors occur during cache update or data processing.
     *
     * Usage Example:
     * **DO NOT USE THIS METHOD DIRECTLY, IT IS INTERNAL FOR CACHE UPDATES**
     *
     * This method is not meant for direct external use but serves as an internal function for handling cache updates
     * when items are updated through PUT or PATCH requests.
     *
     * Note: This method should not be used directly. It is designed to update the cache based on the provided JSON response
     * and request details. It can throw a `Throwable` in case of errors during cache updates or data processing.
     *
     * @see PUT
     * @see PATCH
     * @see JsonFlatter
     * @see updateCachedItem
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> handleUpdateItem(
        json: JsonElement,
        request: HttpRequestBuilder,
        builder: StrapiRequestBuilder
    ): T {
        // Convert the JSON response to an object of type [T]
        val response = JsonFlatter.flat<T>(json).convert<T>()
        // Extract the API path and request class name from the request details
        val apiPath = request.url.encodedPath
        // Get the class name of the request type [T] if available (used for caching) or use simple name of [T] as a fallback (e.g., "User")
        val requestClassName = builder.requestClassName ?: T::class.simpleName ?: ""
        // Convert the response to a JSON element to be able to cache it in the local database
        val responseJson = Json.encodeToJsonElement(serializer<T>(), response)

        // Get the JSON object from the response JSON element if available or use an empty JSON object as a fallback
        val jsonObject = responseJson.jsonObject["data"]?.jsonObject.orEmpty()

        // Convert the JSON object to a JSON string to be able to cache it in the local database
        val jsonContent = Json.encodeToString(jsonObject)

        // Get the model version from the request type [T] if available (used for caching) or use 1 as a fallback
        val modelVersion = serializer<T>().getModelVersion()

        // Update cached data based on the API path, model version, request class name, JSON content, and element ID
        updateCachedItem(
            apiPath,
            modelVersion,
            requestClassName,
            jsonContent,
            // Get the element ID from the JSON object if available or use an empty string as a fallback
            elementId = jsonObject["id"]?.jsonPrimitive?.content.orEmpty()
        )

        // Return the response
        return response
    }

    /**
     * Update cached data based on specified criteria, including API path, model version, model name, JSON content, and element ID.
     *
     * @param apiPath The API path associated with the cached data.
     * @param modelVersion The version of the data model for serialization.
     * @param modelName The name of the data model (if available).
     * @param jsonContent The JSON content to be stored in the cache.
     * @param elementId The unique identifier of the cached item.
     *
     * @throws Throwable if any errors occur during the data update process.
     *
     * Usage Example:
     * ```kotlin
     * val apiPath = "/users/123"
     * val modelVersion = 1
     * val modelName = "User"
     * val jsonData = "{\"name\":\"John\",\"age\":30}"
     * val elementId = "123"
     * updateCachedItem(apiPath, modelVersion, modelName, jsonData, elementId)
     * ```
     *
     * In this example, the function is used to update cached data based on the specified criteria.
     *
     * Note: This function is designed to update cached data with the provided JSON content and can throw a `Throwable`
     * in case of errors during the data update process.
     *
     * @see localDataRepository
     */
    @Throws(Throwable::class)
    suspend fun updateCachedItem(
        apiPath: String,
        modelVersion: Int,
        modelName: String,
        jsonContent: String,
        elementId: String
    ) {
        // Get the element ID as an integer if available (used for caching) or use null as a fallback
        val elementIdInt = elementId.toIntOrNull()

        // Update cached data based on the API path, element ID, model version, model name, and JSON content
        localDataRepository.insertOrUpdateContentData(
            modelVersion = modelVersion,
            modelType = modelName.nullIfEmpty(),
            jsonContent,
            apiPath,
            elementIdInt
        )
    }

    /**
     * Perform an HTTP DELETE request and process the response of type [T], while optionally deleting cached data.
     * This function is designed for use with the Strapi API.
     *
     * @param requestBuilder A lambda function that allows you to customize the request using [StrapiRequestBuilder].
     * @return An object of type [T] representing the parsed response from the DELETE request.
     *
     * @throws Throwable if any errors occur during the request, response processing, or cache deletion.
     *
     * Usage Example:
     * ```kotlin
     * val deletedUser: User = delete {
     *     endpoint("/users/123")
     *     // Add headers or other request customizations if needed
     * }
     * ```
     *
     * In this example, an HTTP DELETE request is made to delete a user, and the response is processed and cached data is optionally deleted.
     *
     * Note: The [reified] type parameter [T] is used to determine the expected type of the response. The function provides
     * flexibility to customize the DELETE request and can throw a `Throwable` in case of errors during the request, response processing, or cache deletion.
     *
     * @see StrapiRequestBuilder
     * @see buildRequest
     * @see HttpMethod
     * @see JsonElement
     * @see JsonFlatter
     * @see deleteCachedItem
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        // Create a new StrapiRequestBuilder
        val builder = StrapiRequestBuilder()
        // Execute the provided lambda to configure the request
        builder.requestBuilder()
        // Build the HTTP DELETE request and extract the API path
        val request = buildRequest(builder, HttpMethod.Delete)

        // Get the API path from the request URL
        val apiPath = request.url.encodedPath

        // Get the model serializer, model version, and request class name from the builder
        val modelSerializer = builder.modelSerializer
        // Get the model version from the request type [T] if available (used for caching) or use 1 as a fallback
        val modelVersion = modelSerializer?.getModelVersion()
        // Get the class name of the request type [T] if available (used for caching) or use simple name of [T] as a fallback (e.g., "User")
        val requestClassName = builder.requestClassName

        // Send the HTTP DELETE request and get the response as a JSON element
        val json =
            httpClient.delete(request).body<JsonElement>()
        // Process the response based on the type [T]
        val response = if (T::class.simpleName == Unit::class.simpleName) {
            // If the type [T] is Unit, return Unit
            Unit as T
        } else {
            // If the type [T] is not Unit, flatten the JSON response and convert it to the specified type [T]
            JsonFlatter.flat<T>(json).convert()
        }

        // Extract the element ID from the API path and delete cached data based on the API path
        val elementId = apiPath.split("/").lastOrNull()

        // Delete cached data based on the API path, element ID, model version, and request class name
        deleteCachedItem(
            apiPath,
            elementId,
            modelVersion,
            requestClassName
        )

        // Return the response
        return response
    }

    /**
     * Delete cached data based on specified criteria, including API path, element ID, model version, and request class name.
     *
     * @param apiPath The API path associated with the cached data.
     * @param elementId The unique identifier of the cached item (if applicable).
     * @param modelVersion The version of the data model (if applicable).
     * @param requestClassName The class name of the request (if applicable).
     *
     * @throws Throwable if any errors occur during the data deletion process.
     *
     * Usage Example:
     * ```kotlin
     * val apiPath = "/users/123"
     * val elementId = "123"
     * val modelVersion = 1
     * val requestClassName = "UserRequest"
     * deleteCachedItem(apiPath, elementId, modelVersion, requestClassName)
     * ```
     *
     * In this example, the function is used to delete cached data based on the specified criteria.
     *
     * Note: This function provides flexibility in specifying the criteria for data deletion and can throw a `Throwable`
     * in case of errors during the deletion process.
     *
     * @see localDataRepository
     */
    @Throws(Throwable::class)
    suspend fun deleteCachedItem(
        apiPath: String,
        elementId: String?,
        modelVersion: Int?,
        requestClassName: String?
    ) {
        // Get the element ID as an integer if available (used for caching) or use null as a fallback
        val elementIdInt = elementId?.toIntOrNull()
        if (elementIdInt != null && requestClassName != null) {
            // If the element ID and request class name are available, delete the cached data by model ID and model type
            localDataRepository.deleteContentDataByModelIdAndModelType(
                elementIdInt,
                requestClassName
            )
        } else if (elementIdInt == null && modelVersion != null) {
            // If the element ID is not available and the model version is available, delete the cached data by model version and API URL
            localDataRepository.deleteContentDataByModelVersionAndApiUrl(
                modelVersion,
                apiPath
            )
        } else {
            // If the element ID and model version are not available, delete the cached data by API URL
            localDataRepository.deleteContentDataByApiUrl(apiPath)
        }
    }

    /**
     * Retrieve and return the content of a URL as a byte array through an HTTP GET request.
     *
     * @param url The URL from which to retrieve the content as a byte array.
     * @return A byte array containing the content of the specified URL.
     *
     * @throws Throwable if any errors occur during the HTTP request or response handling.
     *
     * Usage Example:
     * ```kotlin
     * val imageUrl = "https://example.com/image.jpg"
     * val imageBytes: ByteArray = getBytesFromUrl(imageUrl)
     * ```
     *
     * In this example, the function is used to fetch the content of an image from a given URL and store it as a byte array.
     *
     * Note: This function is designed to handle HTTP GET requests and can throw a `Throwable` in case of errors.
     *
     * @see httpClient
     */
    @Throws(Throwable::class)
    suspend fun getBytesFromUrl(url: String): ByteArray {
        val httpResponse = httpClient.get(url)
        return httpResponse.body()
    }
}

/**
 * Customized configuration for a [Json] object used for JSON serialization and deserialization.
 *
 * - [ignoreUnknownKeys]: Enables ignoring unknown JSON properties when deserializing, allowing partial deserialization.
 * - [useAlternativeNames]: Enables the use of alternative property names when serializing and deserializing based on
 *   the provided `@SerialName` annotations.
 * - [encodeDefaults]: Disables encoding default property values in the output JSON.
 * - [explicitNulls]: Disables explicitly encoding null values in the output JSON.
 *
 * Usage Example:
 * ```kotlin
 * val jsonConfig = JsonWithIgnoredUnknownKeys
 * val jsonString = jsonConfig.encodeToString(data)
 * val parsedData = jsonConfig.decodeFromString<MyDataClass>(jsonString)
 * ```
 *
 * In this example, `JsonWithIgnoredUnknownKeys` is used to configure JSON serialization and deserialization.
 *
 * Note: This configuration is designed to allow ignoring unknown keys, using alternative names, and customizing
 * the encoding behavior of a JSON object.
 *
 * @see Json
 */
val JsonWithIgnoredUnknownKeys = Json {
    ignoreUnknownKeys = true
    useAlternativeNames = true
    encodeDefaults = false
    explicitNulls = false
}

/**
 * Convert a [JsonElement] into an object of type [T] using Kotlinx.serialization with error handling.
 *
 * @return An object of type [T] representing the parsed JSON content.
 *
 * @throws Throwable if any errors occur during deserialization. It may throw a customized error message
 * "Something went wrong, please try again later" for certain deserialization failures if the network log level
 * is set to NONE.
 *
 * Usage Example:
 * ```kotlin
 * val jsonString = "{\"name\":\"John\",\"age\":30}"
 * val jsonElement = Json.parseToJsonElement(jsonString)
 * val user: User = jsonElement.convert()
 * ```
 *
 * In this example, the `jsonElement` is converted into a `User` object using Kotlinx.serialization.
 *
 * Note: The function handles deserialization errors and, in some cases, provides a custom error message
 * when the network log level is set to NONE.
 *
 * @see JsonElement
 * @see JsonWithIgnoredUnknownKeys
 */
@Throws(Throwable::class)
inline fun <reified T> JsonElement.convert(): T {
    try {
        // Attempt to deserialize the JSON element into an object of type [T]
        return JsonWithIgnoredUnknownKeys.decodeFromString(this.toString())
    } catch (throwable: Throwable) {
        // Handle deserialization errors
        if (throwable is kotlinx.serialization.SerializationException && strapiNetworkLogLevel == NetworkLogLevel.NONE) {
            // Provide a custom error message for certain deserialization failures if network log level is NONE
            throw Throwable("Something went wrong, please try again later")
        } else {
            // Re-throw the original throwable for other deserialization errors
            throw throwable
        }
    }
}
