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
import com.swensonhe.strapikmm.util.Logger
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
            val localData =
                localDataRepository.getDataByModelVersionAndApiName(modelVersion, apiPath)
            if (localData != null) {
                val json = Json.parseToJsonElement(localData.content)
                emit(JsonFlatter.flat<T>(json).convert<T>())
            }
        }

        // get data from remote
        val json = httpClient.get(request).body<JsonElement>()
        val response = JsonFlatter.flat<T>(json).convert<T>()
        // save data to cache
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
            // Then cache the whole list
            localDataRepository.insertOrUpdateData(
                // the api name is the api url itself due to the fact that the api url is unique and we can get the list using different query params
                apiName = apiPath,
                data = Json.encodeToString(json),
                modelVersion = modelVersion,
                modelName = requestClassName,
                isList = false
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

        val apiPath = request.url.encodedPath

        val apiUrl = request.url.buildString()

        // get data from cache if available
        if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE && page == 1) {
            val localData =
                localDataRepository.getDataByModelVersionAndApiName(modelVersion, apiUrl)
            if (localData != null) {
                val jsonArray = Json.parseToJsonElement(localData.content).jsonArray
                val cachedData = jsonArray.map { jsonElement ->
                    Json.decodeFromJsonElement(modelSerializer, jsonElement)
                }.filterNotNull()

                if (cachedData.isNotEmpty()) {
                    emit(
                        PagingResponse(
                            data = cachedData,
                            meta = MetaResponse(
                                Paging(
                                    page = 1,
                                    pageSize = cachedData.size,
                                    total = cachedData.size,
                                    pageCount = 1
                                )
                            )
                        ) as T
                    )
                }
            }
        }

        // get data from api
        //////
        val json = httpClient.get(request).body<JsonElement>()
        val response = JsonFlatter.flat<T>(json).convert<T>()
        /////

        // Then cache the whole list and each item in the list individually
        // if the page is 1 or the paging cache strategy is CACHE_LAST
        if (page == 1) {
            // Coverting the list to json array to be able to cache it

            val responseJson = Json.encodeToJsonElement(serializer<T>(), response)
            val jsonArray = responseJson.jsonObject["data"]?.jsonArray.orEmpty()

            // Cache each item in the list individually so that we can update the cache when the item is updated
            jsonArray.forEach { jsonElement ->
                // get each item content
                val jsonContent = Json.encodeToString(jsonElement)

                // get each item id
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content

                // cache each item individually if it has an id
                if (id != null) {
                    localDataRepository.insertOrUpdateData(
                        // the api name is the api path + the id due to the fact that each item has a unique id and each item details can be fetched using the id
                        apiName = "$apiPath/$id",
                        data = jsonContent,
                        modelVersion = modelVersion,
                        modelName = builder.requestClassName ?: "",
                        isList = false
                    )
                }
            }

            // get the whole list json content
            val jsonContent = Json.encodeToString(jsonArray)

            // Then cache the whole list
            localDataRepository.insertOrUpdateData(
                // the api name is the api url itself due to the fact that the api url is unique and we can get the list using different query params
                apiName = apiUrl,
                data = jsonContent,
                modelVersion = modelVersion,
                modelName = builder.requestClassName ?: "",
                isList = true
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

            val fetchStrategy = builder.requestFetchStrategy

            val request = buildRequest(builder, HttpMethod.Get.value)

            val apiPath = request.url.encodedPath

            // get data from cache if available
            if (fetchStrategy == FetchStrategy.CACHE_THEN_REMOTE) {
                val localData =
                    localDataRepository.getDataByModelVersionAndApiName(modelVersion, apiPath)
                if (localData != null) {
                    val cachedData = Json.decodeFromString(modelSerializer, localData.content)
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
            localDataRepository.insertOrUpdateData(
                // the api name is the api path that include the id due to the fact that each item has a unique id and each item details can be fetched using the id
                apiName = apiPath,
                data = jsonContent,
                modelVersion = modelVersion,
                modelName = T::class.simpleName ?: "",
                isList = false
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
        val json =
            httpClient.patch(buildRequest(builder, HttpMethod.Patch.value)).body<JsonElement>()
        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
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
            val response = JsonFlatter.flat<T>(json).convert<T>()

            Logger("Strapi").log("response: $response")
            val apiPath = request.url.encodedPath
            Logger("Strapi").log("apiPath: $apiPath")
            val requestClassName = builder.requestClassName ?: T::class.simpleName ?: ""
            val responseJson = Json.encodeToJsonElement(serializer<T>(), response)
            val jsonObject = responseJson.jsonObject["data"]?.jsonObject.orEmpty()

            val jsonContent = Json.encodeToString(jsonObject)

            Logger("Strapi").log("jsonContent: $jsonContent")

            val modelVersion = serializer<T>().getModelVersion()

            Logger("Strapi").log("modelVersion: $modelVersion")
            updateCachedItem(
                apiPath,
                modelVersion,
                requestClassName,
                jsonContent,
                elementId = jsonObject["id"]?.jsonPrimitive?.content.orEmpty()
            )
            response
        }
    }

    @Throws(Throwable::class)
    suspend fun updateCachedItem(
        apiPath: String,
        modelVersion: Int,
        modelName: String,
        jsonContent: String,
        elementId: String
    ) {

        Logger("Strapi").log("updateCachedItem")
        Logger("Strapi").log("apiPath: $apiPath")
        Logger("Strapi").log("modelVersion: $modelVersion")
        Logger("Strapi").log("modelName: $modelName")
        Logger("Strapi").log("jsonContent: $jsonContent")
        Logger("Strapi").log("elementId: $elementId")

        // update the oneItem
        localDataRepository.insertOrUpdateData(
            // the api name is the api path that include the id due to the fact that each item has a unique id and each item details can be fetched using the id
            apiName = apiPath,
            data = jsonContent,
            modelVersion = modelVersion,
            modelName = modelName,
            isList = false
        )

        // Get all the list with the same modelVersion and modelName
        val allListItems =
            localDataRepository.getAllListDataByModelVersionAndModelName(modelVersion, modelName)
        allListItems.forEach {
            val apiName = it.apiName
            val data = it.content
            val jsonArray = Json.parseToJsonElement(data).jsonArray
            val updatedArray = jsonArray.map { jsonElement ->
                if (jsonElement.jsonObject["id"]?.jsonPrimitive?.content == elementId) {
                    val updatedJson = Json.parseToJsonElement(jsonContent)
                    updatedJson
                } else {
                    jsonElement
                }
            }

            // update the list
            localDataRepository.insertOrUpdateData(
                // the api name is the api path that include the id due to the fact that each item has a unique id and each item details can be fetched using the id
                apiName = apiName,
                data = Json.encodeToString(updatedArray),
                modelVersion = modelVersion,
                modelName = modelName,
                isList = true
            )
        }
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val request = buildRequest(builder, HttpMethod.Delete.value)
        val apiPath = request.url.encodedPath

        val json =
            httpClient.put(request).body<JsonElement>()

        val response = if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
        }
        val elementId = apiPath.split("/").lastOrNull()
        if (elementId != null) {
            deleteCachedItem(apiPath, elementId)
        }
        return response
    }

    @Throws(Throwable::class)
    suspend fun deleteCachedItem(
        apiPath: String,
        elementId: String
    ) {
        val elementInfo = localDataRepository.getDataByApiName(apiName = apiPath) ?: return
        // update the oneItem
        localDataRepository.deleteDataByApiName(apiName = apiPath)

        // Get all the list with the same modelVersion and modelName
        val allListItems = localDataRepository.getAllListDataByModelVersionAndModelName(
            elementInfo.modelVersion,
            elementInfo.modelName
        )
        allListItems.forEach {
            val apiName = it.apiName
            val data = it.content
            val jsonArray = Json.parseToJsonElement(data).jsonArray
            val updatedArray = jsonArray.mapNotNull { jsonElement ->
                if (jsonElement.jsonObject["id"]?.jsonPrimitive?.content == elementId) {
                    null
                } else {
                    jsonElement
                }
            }

            // update the list
            localDataRepository.insertOrUpdateData(
                // the api name is the api path that include the id due to the fact that each item has a unique id and each item details can be fetched using the id
                apiName = apiName,
                data = Json.encodeToString(updatedArray),
                modelVersion = it.modelVersion,
                modelName = it.modelName,
                isList = true
            )
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
