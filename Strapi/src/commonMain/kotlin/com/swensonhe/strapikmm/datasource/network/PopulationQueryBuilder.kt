package com.swensonhe.strapikmm.datasource.network

class PopulationQueryBuilder {
    private val populations: MutableMap<String, Boolean> = mutableMapOf()

    fun populate(key: String, excludeFromDefault: Boolean = false) = apply {
        populations[key] = excludeFromDefault
    }

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

    fun build() = populations
}

enum class PopulationType {
    ALL,
    DEFAULT
}