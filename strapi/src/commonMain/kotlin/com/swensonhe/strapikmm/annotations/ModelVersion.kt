@file:OptIn(ExperimentalSerializationApi::class)

package com.swensonhe.strapikmm.annotations

import kotlinx.serialization.*


/**
 * An annotation used to specify the version of a data model.
 * This annotation can be applied to a Kotlin class definition to indicate the version
 * of the data model represented by that class.
 *
 * @property version The version number of the data model.
 */
@Target(AnnotationTarget.CLASS)
annotation class ModelVersion(val version: Int)

/**
 * Extension function for [KSerializer] that retrieves the version of a data model
 * by inspecting the annotations applied to the serializer's descriptor.
 *
 * @receiver The KSerializer for a specific data model.
 * @return The version of the data model, as specified by the [ModelVersion] annotation,
 *         or a default value of 1 if the annotation is not found.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T> KSerializer<T>.getModelVersion(): Int {
    // Try to find a ModelVersion annotation in the descriptor's annotations
    val modelVersion = descriptor.annotations.find { it is ModelVersion } as? ModelVersion
    // If found, return the version specified in the annotation; otherwise, return a default of 1.
    return modelVersion?.version ?: 1
}