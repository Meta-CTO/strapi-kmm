package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.datasource.network.services.strapi.FetchStrategy
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Builder class for creating Strapi HTTP requests.
 */
class StrapiRequestBuilder {
    private lateinit var requestEndpoint: String
    private lateinit var requestFullUrlEndpoint: String
    val contents: MutableList<RequestContent> = mutableListOf()
    var queryBuilder: StrapiQueryBuilder? = null
    var requestFetchStrategy: FetchStrategy = FetchStrategy.CACHE_THEN_REMOTE
    var requestClassName: String? = null
    var modelSerializer: KSerializer<*>? = null

    /**
     * Sets the endpoint for the request.
     *
     * @param endpoint The endpoint URL for the request.
     */
    fun endpoint(endpoint: String) {
        this.requestEndpoint = endpoint
    }

    /**
     * Sets the full URL endpoint for the request.
     *
     * @param endpoint The full URL endpoint for the request.
     */
    fun endpointFullUrl(endpoint: String) {
        requestFullUrlEndpoint = endpoint
    }

    /**
     * Sets the fetch strategy for the request.
     *
     * @param strategy The fetch strategy to use for the request.
     */
    fun fetchStrategy(strategy: FetchStrategy) {
        this.requestFetchStrategy = strategy
    }

    /**
     * Configures the expected response model for the request.
     *
     * @param T The type of the expected response model.
     */
    inline fun <reified T : Any> responseModel() {
        requestClassName = T::class.simpleName
        modelSerializer = serializer<T>()
    }

    /**
     * Sets whether the request should be authenticated.
     *
     * @param isAuthenticated `true` if the request should be authenticated, otherwise `false`.
     */
    fun authenticated(isAuthenticated: Boolean) {
        if (this.contents.any { it is RequestContent.Authentication }) {
            throw IllegalStateException("You can configure the authentication onetime only inside the request")
        }

        contents.add(RequestContent.Authentication(isAuthenticated))
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param key The query parameter key.
     * @param value The query parameter value.
     */
    fun query(key: String, value: String) = apply {
        contents.add(RequestContent.Query(key, value))
    }

    /**
     * Adds a path parameter to the request.
     *
     * @param key The path parameter key.
     * @param value The path parameter value.
     */
    fun path(key: String, value: String) = apply {
        contents.add(RequestContent.Path(key, value))
    }


    /**
     * Sets the request body data and content type.
     *
     * @param T The type of the request body data.
     * @param value The request body data.
     */
    inline fun <reified T> body(value: T) = apply {
        if (this.contents.any { it is RequestContent.Body<*> }) {
            throw IllegalStateException("You can pass only one body data inside the request")
        }

        header(HttpHeaders.ContentType, "application/json")
        val json = Json.encodeToString(value)
        this.contents.add(RequestContent.Body(value, json))
    }

    /**
     * Adds a custom header to the request.
     *
     * @param key The header key.
     * @param value The header value.
     */
    fun header(key: String, value: String) = apply {
        contents.add(RequestContent.Header(key, value))
    }

    /**
     * Sets the StrapiQueryBuilder for building query parameters.
     *
     * @param strapiQueryBuilder The StrapiQueryBuilder configuration.
     */
    fun strapiQueryBuilder(strapiQueryBuilder: StrapiQueryBuilder.() -> Unit = {}) = apply {
        val builder = StrapiQueryBuilder()
        builder.strapiQueryBuilder()
        queryBuilder = builder
    }

    /**
     * Builds the list of request contents, applying path parameter replacements.
     *
     * @return A list of RequestContent items representing the request.
     */
    fun build(): List<RequestContent> {
        // add the query parameters after extracting them from the query builder
        contents.addAll(queryBuilder?.extractQueries().orEmpty())

        // get the path parameters and replace them in the endpoint
        val pathContents = contents.filterIsInstance<RequestContent.Path>()

        // replace the path parameters in the endpoint url
        var updatedUrl =
            if (::requestEndpoint.isInitialized.not() || requestEndpoint.trim().isEmpty()) {
                requestFullUrlEndpoint
            } else {
                requestEndpoint
            }

        // check if the endpoint is a full url or not
        val isFullUrl = ::requestEndpoint.isInitialized.not() || requestEndpoint.trim().isEmpty()

        // iterate through the path parameters and replace them in the endpoint url
        pathContents.forEach {
            // replace the path parameter in the endpoint url
            updatedUrl = updatedUrl.replace("{${it.key}}", it.value)
        }

        // remove the path parameters from the contents
        val updatedContents = contents.filter { it !is RequestContent.Path }.toMutableList()

        // add the updated endpoint to the contents
        updatedContents.add(RequestContent.Endpoint(updatedUrl, isFullUrl))

        return updatedContents
    }
}


class StrapiQueryBuilder {

    var filters: MutableMap<String, MutableList<String>> = mutableMapOf()
    var pagingData: PagingData? = null
    private var currentFilterIndex = 0

    /**
     * Adds a filter for a specific field.
     *
     * @param field The field to filter.
     * @param value The filter value.
     * @param filterType The filter type (e.g., OR, AND, or NONE).
     */
    fun add(field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE) =
        apply {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${field}", value)
        }

    /**
     * Adds a filter for a specific field with multiple values.
     *
     * @param field The field to filter.
     * @param values The list of filter values.
     * @param filterType The filter type (e.g., OR, AND, or NONE).
     */
    fun add(
        field: String,
        values: MutableList<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${field}", value)
        }
    }

    /**
     * Adds multiple filters from a map.
     *
     * @param map A map of filters to add.
     */
    fun add(map: Map<String, MutableList<String>>) = apply {
        map.forEach { item ->
            put(item.key, item.value)
        }
    }

    /**
     * Adds filters to populate related entities based on the provided [entityPrefix] and [populationType].
     *
     * @param entityPrefix The prefix for entities to populate.
     * @param populationType The type of population to apply (ALL or DEFAULT).
     * @param queryBuilder The builder for constructing entity population queries.
     */
    fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        queryBuilder: PopulationQueryBuilder
    ) {

        // Determine which entities to populate based on population type and add the corresponding filters.
        val populations = when (populationType) {
            PopulationType.ALL -> queryBuilder.build().keys
            PopulationType.DEFAULT -> queryBuilder.build().filter { !it.value }.keys
        }

        populations.forEach {
            // Populate the entities by appending them to the query.
            populate("$entityPrefix.$it")
        }
    }

    /**
     * Adds filters to populate related entities using a DSL function.
     *
     * @param entityPrefix The prefix for entities to populate.
     * @param populationType The type of population to apply (ALL or DEFAULT).
     * @param populationQueryBuilder A DSL function for building the population queries.
     */
    inline fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        crossinline populationQueryBuilder: PopulationQueryBuilder.() -> Unit = {}
    ) {
        // Create a new instance of PopulationQueryBuilder and apply the DSL function to build population queries.
        val builder = PopulationQueryBuilder()
        builder.populationQueryBuilder()

        // Determine which entities to populate based on population type and add the corresponding filters.
        val populations = when (populationType) {
            PopulationType.ALL -> builder.build().keys
            PopulationType.DEFAULT -> builder.build().filter { !it.value }.keys
        }

        populations.forEach {
            // Populate the entities by appending them to the query.
            populate("$entityPrefix.$it")
        }
    }

    /**
     * Adds a "populate" filter to the query, specifying which related entities to populate.
     *
     * @param key The key for entities to populate.
     */
    fun populate(key: String) = apply {
        put("populate", key)
    }

    /**
     * Adds a "groupBy" filter to the query to group the results by a specific field.
     *
     * @param key The key to use for grouping.
     */
    fun groupBy(key: String) = apply {
        put("groupBy", key)
    }

    /**
     * Adds an "equalTo" filter to the query, filtering by equality for a list of values within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun equalTo(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        // Split the field into its component parts.
        val splitField = field.split(".")
        // join the field parts back together with brackets to indicate the field path.
        val updatedField = splitField.joinToString("") { "[$it]" }
        // Iterate through the values and add the filter for each value.
        values.forEach { value ->

            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                // If the filter type is NONE, then don't add a filter index.
                ""
            } else {
                // If the filter type is not NONE, then add a filter index.
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            // Add the filter to the query.
            put("filters${filterType.type}$filterIndex${updatedField}[\$eq]", value)
        }
    }

    /**
     * Adds an "equalTo" filter to the query, filtering by equality for a single value within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun equalTo(
        field: String,
        value: String,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        // Split the field into its component parts.
        val splitField = field.split(".")
        // join the field parts back together with brackets to indicate the field path.
        val updatedField = splitField.joinToString("") { "[$it]" }
        // Add the filter to the query.
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            // If the filter type is NONE, then don't add a filter index.
            ""
        } else {
            // If the filter type is not NONE, then add a filter index.
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        // Add the filter to the query.
        put("filters${filterType.type}$filterIndex${updatedField}[\$eq]", value)
    }

    /**
     * Adds a "notEqualTo" filter to the query, filtering for inequality for a single value within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notEqualTo(
        field: String, value: String,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        // Split the field into its component parts.
        val splitField = field.split(".")
        // join the field parts back together with brackets to indicate the field path.
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        // Add the filter to the query.
        put("filters${filterType.type}$filterIndex${updatedField}[\$ne]", value)
    }

    /**
     * Adds a "notEqualTo" filter to the query, filtering for inequality for a list of values within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notEqualTo(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$ne]", value)
        }
    }

    /**
     * Adds a "lessThan" filter to the query, filtering for values less than the specified value within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun lessThan(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$lt]", value)
    }

    /**
     * Adds a "lessThan" filter to the query, filtering for values less than the specified values within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun lessThan(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$lt]", value)
        }
    }

    /**
     * Adds a "greaterThan" filter to the query, filtering for values greater than the specified value within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun greaterThan(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$gt]", value)
    }

    /**
     * Adds a "greaterThan" filter to the query, filtering for values greater than the specified values within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun greaterThan(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$gt]", value)
        }
    }

    /**
     * Adds a "lessThanOrEqual" filter to the query, filtering for values less than or equal to the specified value
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun lessThanOrEqual(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$lte]", value)
    }

    /**
     * Adds a "greaterThanOrEqual" filter to the query, filtering for values greater than or equal to the specified value
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param value The value for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun greaterThanOrEqual(
        field: String, value: String, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$gte]", value)
    }

    /**
     * Adds an "includedIn" filter to the query, filtering for values that are included in the specified list of values
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun includedIn(
        field: String, value: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$in]", it)
        }
    }

    /**
     * Adds a "notIncludedIn" filter to the query, filtering for values that are not included in the specified list of values
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notIncludedIn(
        field: String, value: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$notIn]", it)
        }
    }

    /**
     * Adds a "contains" filter to the query, filtering for values that contain the specified values
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun contains(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$contains]", value)
        }
    }

    /**
     * Adds a "containsCaseInsensitive" filter to the query, filtering for values that contain the specified values
     * in a case-insensitive manner within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun containsCaseInsensitive(
        field: String, values: List<String>, filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$containsi]", it)
        }
    }

    /**
     * Adds a "notContains" filter to the query, filtering for values that do not contain the specified values
     * within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notContains(
        field: String, values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$ncontain]", it)
        }
    }

    /**
     * Adds a "containsCaseSensitive" filter to the query, filtering for values that contain the specified values
     * in a case-sensitive manner within the specified field.
     *
     * @param field The field to filter on.
     * @param value The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun containsCaseSensitive(
        field: String, value: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        value.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$containss]", it)
        }
    }

    /**
     * Adds a "notContainsCaseSensitive" filter to the query, filtering for values that do not contain the specified values
     * in a case-sensitive manner within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notContainsCaseSensitive(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach { value ->
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$ncontainss]", value)
        }
    }

    /**
     * Adds a "notContainsCaseInsensitive" filter to the query, filtering for values that do not contain the specified values
     * in a case-insensitive manner within the specified field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notContainsCaseInsensitive(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$notContainsi]", it)
        }
    }

    /**
     * Adds a "nullable" filter to the query, filtering for fields that are nullable or not nullable.
     *
     * @param field The field to filter on.
     * @param value Whether the field should be nullable or not (true for nullable, false for not nullable).
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun nullable(
        field: String,
        value: Boolean,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$null]", value.toString())
    }

    /**
     * Adds a "notNullable" filter to the query, filtering for fields that are not nullable or nullable.
     *
     * @param field The field to filter on.
     * @param value Whether the field should be not nullable or nullable (true for not nullable, false for nullable).
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun notNullable(
        field: String,
        value: Boolean,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        val filterIndex = if (filterType == StrapiFilterType.NONE) {
            ""
        } else {
            val filter = "[$currentFilterIndex]"
            currentFilterIndex++
            filter
        }
        put("filters${filterType.type}$filterIndex${updatedField}[\$notNull]", value.toString())
    }

    /**
     * Populates entities based on the specified population type and query builder.
     *
     * @param populationType The type of population to apply (default is ALL).
     * @param queryBuilder The query builder for population.
     */
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

    /**
     * Populates entities based on the specified population type and a population query builder function.
     *
     * @param populationType The type of population to apply (default is ALL).
     * @param populationQueryBuilder The function for configuring the population query builder.
     */
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

    /**
     * Adds a "between" filter to the query, filtering for values within a specified range within the field.
     *
     * @param field The field to filter on.
     * @param values The range values (e.g., ["value1", "value2"]).
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun between(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$between]", it)
        }
    }

    /**
     * Adds a "startsWith" filter to the query, filtering for values that start with the specified values within the field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun startsWith(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$startsWith]", it)
        }
    }

    /**
     * Adds an "endsWith" filter to the query, filtering for values that end with the specified values within the field.
     *
     * @param field The field to filter on.
     * @param values The list of values for filtering.
     * @param filterType The type of filter to apply (default is NONE).
     */
    fun endsWith(
        field: String,
        values: List<String>,
        filterType: StrapiFilterType = StrapiFilterType.NONE
    ) = apply {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        values.forEach {
            val filterIndex = if (filterType == StrapiFilterType.NONE) {
                ""
            } else {
                val filter = "[$currentFilterIndex]"
                currentFilterIndex++
                filter
            }
            put("filters${filterType.type}$filterIndex${updatedField}[\$endsWith]", it)
        }
    }

    /**
     * Adds a "sortBy" field to the query for sorting results by a specified field and sort type.
     *
     * @param field The field to sort by.
     * @param type The sorting type (e.g., ASC or DESC).
     */
    fun sortBy(field: String, type: StrapiSortType) {
        val splitField = field.split(".")
        val updatedField = splitField.joinToString("") { "[$it]" }
        put("sort$updatedField", type.type)
    }

    /**
     * Sets the paging data for the query.
     *
     * @param page The page number.
     * @param pageSize The size of each page.
     */
    fun paging(page: Int, pageSize: Int) {
        pagingData = PagingData(page, pageSize)
        put("pagination[page]", page.toString())
        put("pagination[pageSize]", pageSize.toString())
        put("pagination[withCount]", true.toString())
    }

    /**
     * Adds a single value to the filters with the specified key.
     *
     * @param key The key for the filter.
     * @param value The value to add to the filter.
     */
    private fun put(key: String, value: String) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.add(value)
    }

    /**
     * Adds a list of values to the filters with the specified key.
     *
     * @param key The key for the filter.
     * @param value The list of values to add to the filter.
     */
    private fun put(key: String, value: MutableList<String>) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.addAll(value)
    }
}

/**
 * Enumeration representing the type of sorting order in Strapi sorting.
 *
 * @param type The string representation of the sort type.
 */
enum class StrapiSortType(val type: String) {
    /**
     * Represents ascending sorting order.
     */
    ASC("asc"),

    /**
     * Represents descending sorting order.
     */

    DESC("desc"),
}

/**
 * Enumeration representing the type of logical filter operation in Strapi filtering.
 *
 * @param type The string representation of the filter type.
 */
enum class StrapiFilterType(val type: String) {
    /**
     * Represents the logical OR filter type.
     */
    OR("[\$or]"),

    /**
     * Represents the logical AND filter type.
     */
    AND("[\$and]"),

    /**
     * Represents no specific filter type.
     */
    NONE("");
}

/**
 * Represents the paging information.
 *
 * @param page The current page number.
 * @param pageSize The number of items per page.
 */
data class PagingData(val page: Int, val pageSize: Int)

/**
 * A sealed class representing various components of an HTTP request.
 */
sealed class RequestContent {
    /**
     * Represents a query parameter with a key and value.
     *
     * @param key The query parameter key.
     * @param value The query parameter value.
     */
    class Query(val key: String, val value: String) : RequestContent()

    /**
     * Represents an authentication header indicating whether the request is authenticated.
     *
     * @param isAuthenticated `true` if the request is authenticated; otherwise, `false`.
     */
    class Authentication(val isAuthenticated: Boolean) : RequestContent()

    /**
     * Represents a path parameter with a key and value.
     *
     * @param key The path parameter key.
     * @param value The path parameter value.
     */
    class Path(val key: String, val value: String) : RequestContent()

    /**
     * Represents a header with a key and value.
     *
     * @param key The header key.
     * @param value The header value.
     */
    class Header(val key: String, val value: String) : RequestContent()

    /**
     * Represents the request body with a value and its JSON representation.
     *
     * @param value The request body value.
     * @param jsonString The JSON representation of the request body.
     */
    class Body<T>(val value: T, val jsonString: String) : RequestContent()

    /**
     * Represents an endpoint URL with an optional flag to indicate if it's a full URL.
     *
     * @param url The endpoint URL.
     * @param isFullUrl `true` if the URL is a full URL; otherwise, `false`.
     */
    class Endpoint(val url: String, val isFullUrl: Boolean) : RequestContent()
}

/**
 * Extract query parameters from a StrapiQueryBuilder.
 *
 * @return A list of [RequestContent.Query] objects representing the query parameters.
 *
 *
 * This function extracts query parameters from a [StrapiQueryBuilder] and returns them as a list of [RequestContent.Query] objects.
 * It is useful for transforming the filter criteria set in a StrapiQueryBuilder into query parameters that can be included
 * in an HTTP request.
 *
 * @see RequestContent.Query
 * @see StrapiQueryBuilder
 */
fun StrapiQueryBuilder.extractQueries(): List<RequestContent.Query> {
    return this.filters.map { entry ->
        return@map entry.value.map { entryValue ->
            RequestContent.Query(entry.key, entryValue)
        }
    }.flatten()
}
