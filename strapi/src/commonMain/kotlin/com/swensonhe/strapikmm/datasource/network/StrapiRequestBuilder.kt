package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.datasource.network.services.strapi.FetchStrategy
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class StrapiRequestBuilder {
    private lateinit var requestEndpoint: String
    private lateinit var requestFullUrlEndpoint: String
    val contents: MutableList<RequestContent> = mutableListOf()
    var queryBuilder: StrapiQueryBuilder? = null
    var requestFetchStrategy: FetchStrategy = FetchStrategy.CACHE_THEN_REMOTE
    var requestClassName: String? = null
    var modelSerializer: KSerializer<*>? = null

    fun endpoint(endpoint: String) {
        this.requestEndpoint = endpoint
    }

    fun endpointFullUrl(endpoint: String) {
        requestFullUrlEndpoint = endpoint
    }

    fun fetchStrategy(strategy: FetchStrategy) {
        this.requestFetchStrategy = strategy
    }

    inline fun <reified T : Any> responseModel() {
        requestClassName = T::class.simpleName
        modelSerializer = serializer<T>()
    }


    fun authenticated(isAuthenticated: Boolean) {
        if (this.contents.any { it is RequestContent.Authentication }) {
            throw IllegalStateException("You can configure the authentication onetime only inside the request")
        }

        contents.add(RequestContent.Authentication(isAuthenticated))
    }

    fun query(key: String, value: String) = apply {
        contents.add(RequestContent.Query(key, value))
    }

    fun path(key: String, value: String) = apply {
        contents.add(RequestContent.Path(key, value))
    }

    inline fun <reified T> body(value: T) = apply {
        if (this.contents.any { it is RequestContent.Body<*> }) {
            throw IllegalStateException("You can pass only one body data inside the request")
        }

        header(HttpHeaders.ContentType, "application/json")
        val json = Json.encodeToString(value)
        this.contents.add(RequestContent.Body(value, json))
    }

    fun header(key: String, value: String) = apply {
        contents.add(RequestContent.Header(key, value))
    }

    fun strapiQueryBuilder(strapiQueryBuilder: StrapiQueryBuilder.() -> Unit = {}) = apply {
        val builder = StrapiQueryBuilder()
        builder.strapiQueryBuilder()
        queryBuilder = builder
    }

    fun build(): List<RequestContent> {
        contents.addAll(queryBuilder?.extractQueries().orEmpty())

        val pathContents = contents.filterIsInstance<RequestContent.Path>()

        var updatedUrl =
            if (::requestEndpoint.isInitialized.not() || requestEndpoint.trim().isEmpty()) {
                requestFullUrlEndpoint
            } else {
                requestEndpoint
            }

        val isFullUrl = ::requestEndpoint.isInitialized.not() || requestEndpoint.trim().isEmpty()

        pathContents.forEach {
            updatedUrl = updatedUrl.replace("{${it.key}}", it.value)
        }

        val updatedContents = contents.filter { it !is RequestContent.Path }.toMutableList()

        updatedContents.add(RequestContent.Endpoint(updatedUrl, isFullUrl))

        return updatedContents
    }

}

class StrapiQueryBuilder {

    var filters: MutableMap<String, MutableList<String>> = mutableMapOf()
    var pagingData: PagingData? = null
    private var currentFilterIndex = 0

    fun add(field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE) =
        apply {
            put("filters${filterType.type}[$currentFilterIndex]${field}", value)
        }

    fun add(
        field: String,
        values: MutableList<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${field}", value)
            currentFilterIndex++
        }
    }

    fun add(map: Map<String, MutableList<String>>) = apply {
        map.forEach { item ->
            put(item.key, item.value)
            currentFilterIndex++
        }
    }

    fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        queryBuilder: PopulationQueryBuilder
    ) {

        val populations = when (populationType) {
            PopulationType.ALL -> queryBuilder.build().keys
            PopulationType.DEFAULT -> queryBuilder.build().filter { !it.value }.keys
        }

        populations.forEach {
            populate("$entityPrefix.$it")
        }
    }

    inline fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        crossinline populationQueryBuilder: PopulationQueryBuilder.() -> Unit = {}
    ) {
        val builder = PopulationQueryBuilder()
        builder.populationQueryBuilder()

        val populations = when (populationType) {
            PopulationType.ALL -> builder.build().keys
            PopulationType.DEFAULT -> builder.build().filter { !it.value }.keys
        }

        populations.forEach {
            populate("$entityPrefix.$it")
        }
    }

    fun populate(key: String) = apply {
        put("populate", key)
    }

    fun groupBy(key: String) = apply {
        val groupByKeys = filters.keys.filter { it.startsWith("groupBy") }
        put("groupBy[${groupByKeys.size}]", key)
    }

    fun equalTo(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$eq]", value)
            currentFilterIndex++
        }
    }

    fun equalTo(
        field: String,
        value: String,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$eq]", value)
        currentFilterIndex++
    }

    fun notEqualTo(
        field: String, value: String,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$ne]", value)
        currentFilterIndex++
    }

    fun notEqualTo(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$ne]", value)
            currentFilterIndex++
        }
    }

    fun lessThan(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$lt]", value)
        currentFilterIndex++
    }

    fun lessThan(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$lt]", value)
            currentFilterIndex++
        }
    }

    fun greaterThan(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$gt]", value)
        currentFilterIndex++
    }

    fun greaterThan(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$gt]", value)
            currentFilterIndex++
        }
    }

    fun lessThanOrEqual(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$lte]", value)
        currentFilterIndex++
    }

    fun greaterThanOrEqual(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$gte]", value)
        currentFilterIndex++
    }

    fun includedIn(
        field: String, value: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$in]", it)
            currentFilterIndex++
        }
    }

    fun notIncludedIn(
        field: String, value: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$notIn]", it)
            currentFilterIndex++
        }
    }

    fun contains(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$contains]", value)
            currentFilterIndex++
        }
    }

    fun containsCaseInsensitive(
        field: String, values: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$containsi]", it)
            currentFilterIndex++
        }
    }

    fun notContains(
        field: String, values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$ncontain]", it)
            currentFilterIndex++
        }
    }

    fun containsCaseSensitive(
        field: String, value: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$containss]", it)
            currentFilterIndex++
        }
    }

    fun notContainsCaseSensitive(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$ncontainss]", value)
            currentFilterIndex++
        }
    }

    fun notContainsCaseInsensitive(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$notContainsi]", it)
            currentFilterIndex++
        }
    }

    fun nullable(
        field: String,
        value: Boolean,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$null]", value.toString())
        currentFilterIndex++
    }

    fun notNullable(
        field: String,
        value: Boolean,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$notNull]", value.toString())
        currentFilterIndex++
    }

    fun populateEntity(
        populationType: PopulationType = PopulationType.ALL,
        queryBuilder: PopulationQueryBuilder,
    ) {
        val populations = when (populationType) {
            PopulationType.ALL -> queryBuilder.build().keys
            PopulationType.DEFAULT -> queryBuilder.build().filter { !it.value }.keys
        }

        populations.forEach {
            populate(it)
        }

    }

    inline fun populateEntity(
        populationType: PopulationType = PopulationType.ALL,
        crossinline populationQueryBuilder: PopulationQueryBuilder.() -> Unit = {}
    ) {
        val queryBuilder = PopulationQueryBuilder()
        queryBuilder.populationQueryBuilder()
        val populations = when (populationType) {
            PopulationType.ALL -> queryBuilder.build().keys
            PopulationType.DEFAULT -> queryBuilder.build().filter { !it.value }.keys
        }

        populations.forEach {
            populate(it)
        }
    }

    fun between(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$between]", it)
            currentFilterIndex++
        }
    }

    fun startsWith(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$startsWith]", it)
            currentFilterIndex++
        }
    }

    fun endsWith(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            put("filters${filterType.type}[$currentFilterIndex]${updatedField}[\$endsWith]", it)
            currentFilterIndex++
        }
    }

    fun sortBy(field: String, type: StrapiSortType) {
        val sortKeys = filters.keys.filter { it.startsWith("sort") }
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("sort[${sortKeys.size}]$updatedField", type.type)
    }

    fun paging(page: Int, pageSize: Int) {
        pagingData = PagingData(page, pageSize)
        put("pagination[page]", page.toString())
        put("pagination[pageSize]", pageSize.toString())
        put("pagination[withCount]", true.toString())
    }

    private fun put(key: String, value: String) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.add(value)
    }

    private fun put(key: String, value: MutableList<String>) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.addAll(value)
    }
}

enum class StrapiSortType(val type: String) {
    ASC("asc"),
    DESC("desc"),
}

enum class StrapiFilterType(val type: String) {
    OR("[\$or]"),
    AND("[\$and]"),
    NONE("");
}

data class PagingData(val page: Int, val pageSize: Int)

sealed class RequestContent {
    class Query(val key: String, val value: String) : RequestContent()
    class Authentication(val isAuthenticated: Boolean) : RequestContent()
    class Path(val key: String, val value: String) : RequestContent()
    class Header(val key: String, val value: String) : RequestContent()
    class Body<T>(val value: T, val jsonString: String) : RequestContent()
    class Endpoint(val url: String, val isFullUrl: Boolean) : RequestContent()
}

fun StrapiQueryBuilder.extractQueries(): List<RequestContent.Query> {
    return this.filters.map { entry ->
        return@map entry.value.map { entryValue ->
            RequestContent.Query(entry.key, entryValue)
        }
    }.flatten()
}
