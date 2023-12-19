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

class StrapiService(
    val httpClient: HttpClient,
    baseUrl: String,
    kmmPreference: KmmPreference,
    context: Any? = null,
) : KmmBaseService(baseUrl, kmmPreference) {

    val localDataRepository by lazy {
        LocalDataRepository(DatabaseDriverFactory(context))
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> get(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()

        val request = buildRequest(builder, HttpMethod.Get.value)
        val json = httpClient.get(request).body<JsonElement>()
        return JsonFlatter.flat<T>(json).convert<T>()
    }

    @Throws(Throwable::class)
    inline fun <reified T> getFlow(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): Flow<T> {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()

        // throw exception if the model is PagingResponse or DataWrapper
        return if (T::class == PagingResponse::class) {
            getPaged(requestBuilder)
        } else if (T::class == DataWrapper::class) {
            getOne(requestBuilder)
        } else {
            getDefault(requestBuilder)
        }
    }

    @Throws(Throwable::class)
    inline fun <reified T> getDefault(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}
    ): Flow<T> = flow {
        // build the builder and the request to extract the path and the url
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()

        val request = buildRequest(builder, HttpMethod.Get.value)
        val apiPath = request.url.encodedPath
        val fetchStrategy = builder.requestFetchStrategy
        val requestClassName = builder.requestClassName ?: T::class.simpleName ?: ""

        val modelVersion = serializer<T>().getModelVersion()

        // get data from cache if available
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
            val localData = if (requestClassName.isNotEmpty()) {
                localDataRepository.getContentDataByModelVersionAndModelTypeAndApiUrl(
                    modelVersion,
                    requestClassName,
                    apiPath
                )
            } else {
                localDataRepository.getContentDataByApiUrl(apiPath)
            }

            if (localData?.content.isNullOrEmpty().not()) {
                val json = JsonWithIgnoredUnknownKeys.parseToJsonElement(localData?.content!!)
                emit(JsonFlatter.flat<T>(json).convert<T>())
            }
        }

        // get data from remote
        val json = httpClient.get(request).body<JsonElement>()
        val response = JsonFlatter.flat<T>(json).convert<T>()
        // save data to cache
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
            // Then cache

            localDataRepository.insertOrUpdateContentData(
                modelVersion,
                requestClassName,
                Json.encodeToString(json),
                apiPath,
                null
            )
        }

        emit(response)
    }

    @Throws(Throwable::class)
    inline fun <reified T> getPaged(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}
    ): Flow<T> = flow {

        // build the builder and the request to extract the path and the url
        val builder = StrapiRequestBuilder()

        builder.requestBuilder()

        val modelSerializer = builder.modelSerializer
            ?: throw Throwable("You must provide the responseType in the requestBuilder")

        val modelVersion = modelSerializer.getModelVersion()

        val page = builder.queryBuilder?.pagingData?.page ?: 1

        val fetchStrategy = builder.requestFetchStrategy

        val request = buildRequest(builder, HttpMethod.Get.value)

        val requestClassName = builder.requestClassName ?: ""

        val apiUrl = request.url.buildString()

        val apiPath = request.url.encodedPath

        // get data from cache if available
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE && page == 1) {
            val localData =
                localDataRepository.getListDataByModelVersionAndApiUrl(modelVersion, apiUrl)
            val listItems = localData.orEmpty().map { localItem ->
                if (localItem.content.isNullOrEmpty().not()) {
                    Json.decodeFromJsonElement(
                        modelSerializer,
                        JsonWithIgnoredUnknownKeys.parseToJsonElement(localItem.content!!)
                    )
                } else {
                    null
                }
            }.filterNotNull()

            if (listItems.isNotEmpty()) {
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

        // get data from api
        //////
        val json = httpClient.get(request).body<JsonElement>()
        val flatResponse = JsonFlatter.flat<T>(json)
        val response = flatResponse.convert<T>()
        /////

        // Then cache the whole list and each item in the list individually
        // if the page is 1 or the paging cache strategy is CACHE_LAST
        if (page == 1) {
            // Converting the list to json array to be able to cache it
            val jsonArray = flatResponse.jsonObject["data"]?.jsonArray.orEmpty()
            val elementsIds = jsonArray.mapNotNull { jsonElement ->
                jsonElement.jsonObject["id"]?.jsonPrimitive?.content
               }.joinToString(",")

            // Cache each item in the list individually so that we can update the cache when the item is updated
            jsonArray.forEach { jsonElement ->
                // get each item content
                val jsonContent = Json.encodeToString(jsonElement)

                // get each item id
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()

                // cache each item individually if it has an id
                if (id != null && requestClassName.isNotEmpty()) {
                    val localCachedItem =
                        localDataRepository.getContentDataByModelTypeAndModelVersionAndModelId(
                            requestClassName,
                            modelVersion,
                            id
                        )

                    // if the item is not cached then cache it
                    if (localCachedItem?.content.isNullOrEmpty()) {
                        localDataRepository.insertOrUpdateContentData(
                            modelVersion,
                            requestClassName,
                            jsonContent,
                            "$apiPath/$id",
                            id
                        )
                    } else {
                        val itemJsonElement = JsonWithIgnoredUnknownKeys.parseToJsonElement(localCachedItem?.content!!)
                        if (itemJsonElement as? JsonObject != null && jsonElement as? JsonObject != null) {
                            val cachedJsonObject = itemJsonElement.toMutableMap()
                            val newJsonObject = jsonElement.toMutableMap()
                            cachedJsonObject.putAll(newJsonObject)
                            val jsonData = JsonObject(cachedJsonObject).toString()

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

            // Then cache the whole list
            localDataRepository.insertOrUpdateListData(
                apiUrl,
                modelVersion,
                requestClassName,
                elementsIds
            )
        }

        emit(response)
    }

    @Throws(Throwable::class)
    inline fun <reified T> getOne(crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {}): Flow<T> =
        flow {

            // build the builder and the request to extract the path and the url
            val builder = StrapiRequestBuilder()
            builder.requestBuilder()

            val modelSerializer = builder.modelSerializer
                ?: throw Throwable("You must provide the responseType in the requestBuilder")
            val modelVersion = modelSerializer.getModelVersion()

            val requestClassName = builder.requestClassName ?: ""

            val fetchStrategy = builder.requestFetchStrategy

            val request = buildRequest(builder, HttpMethod.Get.value)

            val apiPath = request.url.encodedPath

            // We need to get the id from the encoded path which is the last part of the path and it can be like posts/1 or comments/1 .. etc
            val entityId = apiPath.split("/").lastOrNull()?.toInt()

            // get data from cache if available
            if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
                val localData = if (entityId != null && requestClassName.isNotEmpty()) {
                    localDataRepository.getContentDataByModelTypeAndModelVersionAndModelId(
                        requestClassName,
                        modelVersion,
                        entityId
                    )
                } else if (entityId == null && requestClassName.isEmpty()) {
                    localDataRepository.getContentDataByModelVersionAndApiUrl(modelVersion, apiPath)
                } else {
                    localDataRepository.getContentDataByApiUrl(apiPath)
                }

                if (localData?.content.isNullOrEmpty().not()) {
                    val cachedData = Json.decodeFromString(modelSerializer, localData?.content!!)
                    emit(DataWrapper(cachedData) as T)
                }
            }

            // get data from api
            //////
            val json = httpClient.get(request).body<JsonElement>()
            val response = JsonFlatter.flat<T>(json).convert<T>()
            /////

            val responseJson = Json.encodeToJsonElement(serializer<T>(), response)
            val jsonObject = responseJson.jsonObject["data"]?.jsonObject.orEmpty()

            val jsonContent = Json.encodeToString(jsonObject)

            // Then Insert
            localDataRepository.insertOrUpdateContentData(
                modelVersion = modelVersion,
                modelType = requestClassName.nullIfEmpty(),
                jsonContent,
                apiPath,
                entityId
            )

            emit(response)
        }

    @Throws(Throwable::class)
    suspend inline fun <reified T> post(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val json =
            httpClient.post(buildRequest(builder, HttpMethod.Post.value)).body<JsonElement>()
        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
        }
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> patch(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val request = buildRequest(builder, HttpMethod.Patch.value)
        val json = httpClient.patch(request).body<JsonElement>()

        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            handleUpdateItem(json, request, builder)
        }
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> put(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val request = buildRequest(builder, HttpMethod.Put.value)
        val json =
            httpClient.put(request).body<JsonElement>()

        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            handleUpdateItem(json, request, builder)
        }
    }

    /**
     * DON'T USE THIS METHOD DIRECTLY USE [PUT, PATCH] INSTEAD
     * This method is used to update the cache when an item is updated
     * @param json the json response from the api
     * @param request the request that was sent to the api
     * @param builder the request builder
     */
    @Throws(Throwable::class)
    suspend inline fun <reified T> handleUpdateItem(
        json: JsonElement,
        request: HttpRequestBuilder,
        builder: StrapiRequestBuilder
    ): T {
        val response = JsonFlatter.flat<T>(json).convert<T>()
        val apiPath = request.url.encodedPath
        val requestClassName = builder.requestClassName ?: T::class.simpleName ?: ""
        val responseJson = Json.encodeToJsonElement(serializer<T>(), response)
        val jsonObject = responseJson.jsonObject["data"]?.jsonObject.orEmpty()

        val jsonContent = Json.encodeToString(jsonObject)

        val modelVersion = serializer<T>().getModelVersion()

        updateCachedItem(
            apiPath,
            modelVersion,
            requestClassName,
            jsonContent,
            elementId = jsonObject["id"]?.jsonPrimitive?.content.orEmpty()
        )

        return response
    }

    @Throws(Throwable::class)
    suspend fun updateCachedItem(
        apiPath: String,
        modelVersion: Int,
        modelName: String,
        jsonContent: String,
        elementId: String
    ) {
        val elementIdInt = elementId.toIntOrNull()
        localDataRepository.insertOrUpdateContentData(
            modelVersion = modelVersion,
            modelType = modelName.nullIfEmpty(),
            jsonContent,
            apiPath,
            elementIdInt
        )
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val request = buildRequest(builder, HttpMethod.Delete.value)
        val apiPath = request.url.encodedPath

        val modelSerializer = builder.modelSerializer
        val modelVersion = modelSerializer?.getModelVersion()
        val requestClassName = builder.requestClassName


        val json =
            httpClient.delete(request).body<JsonElement>()

        val response = if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
        }
        val elementId = apiPath.split("/").lastOrNull()
        deleteCachedItem(
            apiPath,
            elementId,
            modelVersion,
            requestClassName
        )
        return response
    }

    @Throws(Throwable::class)
    suspend fun deleteCachedItem(
        apiPath: String,
        elementId: String?,
        modelVersion: Int?,
        requestClassName: String?
    ) {
        val elementIdInt = elementId?.toIntOrNull()
        if (elementIdInt != null && requestClassName != null) {
            localDataRepository.deleteContentDataByModelIdAndModelType(
                elementIdInt,
                requestClassName
            )
        } else if (elementIdInt == null && modelVersion != null) {
            localDataRepository.deleteContentDataByModelVersionAndApiUrl(
                modelVersion,
                apiPath
            )
        } else {
            localDataRepository.deleteContentDataByApiUrl(apiPath)
        }
    }

    @Throws(Throwable::class)
    suspend fun getBytesFromUrl(url: String): ByteArray {
        val httpResponse = httpClient.get(url)
        return httpResponse.body()
    }
}

val JsonWithIgnoredUnknownKeys = Json {
    ignoreUnknownKeys = true
    useAlternativeNames = true
    encodeDefaults = false
    explicitNulls = false
    coerceInputValues = true
}

@Throws(Throwable::class)
inline fun <reified T> JsonElement.convert(): T {
    try {
        return JsonWithIgnoredUnknownKeys.decodeFromString(this.toString())
    } catch (throwable: Throwable) {
        if (throwable is kotlinx.serialization.SerializationException && strapiNetworkLogLevel == NetworkLogLevel.NONE) {
            throw Throwable("Something went wrong, please try again later")
        } else {
            throw throwable
        }
    }
}
