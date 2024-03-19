@file:OptIn(ExperimentalSerializationApi::class)

package com.metaCTO.strapikmm.annotations

import kotlinx.serialization.*

@Target(AnnotationTarget.CLASS)
annotation class ModelVersion(val version: Int)

fun <T> KSerializer<T>.getModelVersion(): Int {
    val modelVersion = descriptor.annotations.find { it is ModelVersion } as? ModelVersion
    return modelVersion?.version ?: 1
}