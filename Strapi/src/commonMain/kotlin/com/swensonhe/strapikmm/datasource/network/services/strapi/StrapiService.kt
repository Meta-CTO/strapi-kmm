package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import com.swensonhe.strapikmm.datasource.network.StrapiRequestBuilder
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
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
