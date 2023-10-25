package com.swensonhe.strapikmm.datasource.network

/**
 * Builder for defining population preferences of entities.
 *
 * This class allows you to specify which entities to populate or exclude from default population.
 * It provides methods for configuring entity population based on population type and entity prefixes.
 *
 * Usage Example:
 * ```kotlin
 * val populationBuilder = PopulationQueryBuilder()
 * populationBuilder.populate("author").populate("comments", excludeFromDefault = true)
 *
 * // Define entity population based on a prefix and population type
 * populationBuilder.populateEntity("post", PopulationType.ALL) {
 *     populate("author")
 *     populate("comments", excludeFromDefault = true)
 * }
 *
 * // Build the population preferences
 * val populations = populationBuilder.build()
 * ```
 *
 * In this example, the `PopulationQueryBuilder` class is used to define which entities to populate or exclude from default population.
 * You can specify population preferences using `populate` methods or define population for entities with prefixes using `populateEntity` methods.
 *
 * @see PopulationType
 */
class PopulationQueryBuilder {
    private val populations: MutableMap<String, Boolean> = mutableMapOf()


    /**
     * Populate and define preferences for a specific entity or key.
     *
     * @param key The key or entity name to populate.
     * @param excludeFromDefault Whether to exclude the key or entity from default population.
     *
     * Usage Example:
     * ```kotlin
     * val populationBuilder = PopulationQueryBuilder()
     * populationBuilder.populate("author")
     * populationBuilder.populate("comments", excludeFromDefault = true)
     * ```
     *
     * This function allows you to specify population preferences for a specific entity or key within the population query.
     * You can indicate whether to exclude the specified entity from default population preferences.
     *
     * @see PopulationQueryBuilder
     */
    fun populate(key: String, excludeFromDefault: Boolean = false) = apply {
        populations[key] = excludeFromDefault
    }

    /**
     * Define population preferences for entities with a common prefix.
     *
     * @param entityPrefix The common prefix of the entities to populate.
     * @param populationType The population type (ALL or DEFAULT).
     * @param excludeFromDefault Whether to exclude the populated entities from default population.
     * @param queryBuilder The [PopulationQueryBuilder] for defining entity population.
     */
    fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        excludeFromDefault: Boolean = false,
        queryBuilder: PopulationQueryBuilder
    ) {

        val populations = when (populationType) {
            PopulationType.ALL -> queryBuilder.build()
            PopulationType.DEFAULT -> queryBuilder.build().filter { !it.value }
        }

        populations.forEach {
            populate("$entityPrefix.${it.key}", it.value || excludeFromDefault)
        }

    }

    /**
     * Define population preferences for entities with a common prefix using a lambda.
     *
     * @param entityPrefix The common prefix of the entities to populate.
     * @param populationType The population type (ALL or DEFAULT).
     * @param excludeFromDefault Whether to exclude the populated entities from default population.
     * @param populationQueryBuilder A lambda for defining entity population using another [PopulationQueryBuilder].
     *
     * Usage Example:
     * ```kotlin
     * val populationBuilder = PopulationQueryBuilder()
     * populationBuilder.populateEntity("post", PopulationType.ALL) {
     *     // Define entity population using the lambda
     *     populate("author")
     *     populate("comments", excludeFromDefault = true)
     * }
     * ```
     *
     * This function allows you to define population preferences for entities with a common prefix using a lambda.
     * You can specify the population type, whether to exclude from default population, and use the provided lambda
     * to define the population for the entities sharing the specified prefix.
     *
     * @see PopulationQueryBuilder
     * @see PopulationType
     */
    inline fun populateEntity(
        entityPrefix: String,
        populationType: PopulationType = PopulationType.DEFAULT,
        excludeFromDefault: Boolean = false,
        crossinline populationQueryBuilder: PopulationQueryBuilder.() -> Unit = {}
    ) {
        val builder = PopulationQueryBuilder()
        builder.populationQueryBuilder()

        val populations = when (populationType) {
            PopulationType.ALL -> builder.build()
            PopulationType.DEFAULT -> builder.build().filter { !it.value }
        }

        populations.forEach {
            populate("$entityPrefix.${it.key}", it.value || excludeFromDefault)
        }
    }

    /**
     * Build and retrieve the populated entity preferences.
     *
     * @return A map representing the entity population preferences.
     */
    fun build() = populations
}

/**
 * Enumeration to specify the type of population (ALL or DEFAULT).
 */
enum class PopulationType {
    /** Include all entities in population. */
    ALL,

    /** Include entities in population based on default preferences. */
    DEFAULT
}
