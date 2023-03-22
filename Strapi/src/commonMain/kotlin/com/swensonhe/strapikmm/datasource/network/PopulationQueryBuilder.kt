package com.swensonhe.strapikmm.datasource.network

class PopulationQueryBuilder {
    private val populations: MutableMap<String, Boolean> = mutableMapOf()

    fun populate(key: String, excludeFromDefault: Boolean = false) = apply {
        populations[key] = excludeFromDefault
    }

    fun build() = populations
}

enum class PopulationType {
    ALL,
    DEFAULT
}