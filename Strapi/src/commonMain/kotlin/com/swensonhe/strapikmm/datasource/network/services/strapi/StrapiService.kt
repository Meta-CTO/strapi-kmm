package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.StrapiRequestBuilder
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class StrapiService(
    val httpClient: HttpClient,
    baseUrl: String,
    kmmPreference: KmmPreference
) : KmmBaseService(baseUrl, kmmPreference) {

    @Throws(Throwable::class)
    suspend inline fun <reified T> get(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val json = httpClient.get(
            buildRequest(builder, HttpMethod.Get.value)
        ).body<JsonElement>()
        return JsonFlatter.flat<T>(json).convert<T>()
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
        val json =
            httpClient.put(buildRequest(builder, HttpMethod.Put.value)).body<JsonElement>()
        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
        }
    }

    @Throws(Throwable::class)
    suspend inline fun <reified T> delete(
        crossinline requestBuilder: StrapiRequestBuilder.() -> Unit = {},
    ): T {
        val builder = StrapiRequestBuilder()
        builder.requestBuilder()
        val json =
            httpClient.delete(buildRequest(builder, HttpMethod.Delete.value)).body<JsonElement>()
        return if (T::class.simpleName == Unit::class.simpleName) {
            Unit as T
        } else {
            JsonFlatter.flat<T>(json).convert()
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

inline fun <reified T> JsonElement.convert(): T {
    return JsonWithIgnoredUnknownKeys.decodeFromString(this.toString())
}
