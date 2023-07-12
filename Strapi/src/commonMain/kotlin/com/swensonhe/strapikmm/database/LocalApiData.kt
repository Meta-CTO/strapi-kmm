package com.swensonhe.strapikmm.database

data class LocalApiData(
    val apiName: String,
    val modelVersion: Int,
    val content: String,
    val modelName: String,
    val isList: Boolean
)