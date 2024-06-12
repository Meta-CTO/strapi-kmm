package com.metacto.strapikmm.datasource.network.services.strapi

import com.metacto.strapikmm.datasource.network.NetworkLogConfiguration
import com.metacto.strapikmm.datasource.network.NetworkLogLevel
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
import com.metacto.strapikmm.util.Logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

object JsonFlatter {

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> flat(jsonElement: JsonElement): JsonElement {
        if (NetworkLogConfiguration.logLevel == NetworkLogLevel.ALL) {
            Logger("").log(jsonElement.toString())
        }

        val descriptor = serializer(typeOf<T>()).descriptor
        val elementNames = descriptor.elementNames

        return when (jsonElement) {
            is JsonObject -> {
                val map = mutableMapOf<String, JsonElement>()
                elementNames.forEachIndexed { index, elementName ->
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    val jsonNames = mutableListOf<String>()
                    val annotations = descriptor.getElementAnnotations(index)
                        .filter { it is JsonNames || it is SerialName }

                    annotations.forEach { annotation ->
                        if (annotation is SerialName) {
                            jsonNames.add(annotation.value)
                        } else if (annotation is JsonNames) {
                            jsonNames.addAll(
                                annotation.names.sortedBy { it.split(".").size }
                            )
                        }
                    }

                    if (jsonNames.isEmpty()) {
                        jsonNames.add(elementName)
                    }

                    // Default value if the item not presented in the json
                    map[elementName] = JsonNull

                    jsonNames.forEach { element ->
                        val value = parse(element, jsonElement, childDescriptor)
                        if (value != DummyObject && value != null) {
                            map[elementName] = value
                        }
                    }
                }

                JsonObject(map)
            }
            is JsonArray -> {
                val jsonElements = jsonElement.mapIndexed { index, element ->
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    parse(element, childDescriptor)
                }

                JsonArray(jsonElements)
            }
            else -> {
                throw NetworkErrorMapper.mapToAppException(
                    "Malformed JSON passed to parser, expected object or array but got $jsonElement",
                    -1
                )
            }
        }
    }

    @ExperimentalSerializationApi
    fun parse(json: JsonObject, descriptor: SerialDescriptor): JsonObject {
        if(json.isEmpty()) {
            return json
        }

        if (descriptor.kind == PolymorphicKind.SEALED) {
            return json
        }

        val map = mutableMapOf<String, JsonElement>()
        val elementNames = descriptor.elementNames
        elementNames.forEachIndexed { index, elementName ->
            val childDescriptor = descriptor.getElementDescriptor(index)
            val jsonNames = mutableListOf<String>()
            val annotations = descriptor.getElementAnnotations(index)
                .filter { it is JsonNames || it is SerialName }

            annotations.forEach { annotation ->
                if (annotation is SerialName) {
                    jsonNames.add(annotation.value)
                } else if (annotation is JsonNames) {
                    jsonNames.addAll(
                        annotation.names.sortedBy { it.split(".").size }
                    )
                }
            }

            if (jsonNames.isEmpty()) {
                jsonNames.add(elementName)
            }

            // Default value if the item not presented in the json
            map[elementName] = JsonNull

            jsonNames.forEach { element ->
                val value = parse(element, json, childDescriptor)
                if (value != DummyObject && value != null) {
                    map[elementName] = value
                }
            }
        }

        return JsonObject(map)
    }

    @ExperimentalSerializationApi
    fun parse(
        elementName: String,
        jsonObject: JsonObject,
        descriptor: SerialDescriptor
    ): JsonElement? {
        if (jsonObject.keyExists(elementName).not()) {
            return DummyObject
        } else if (elementName.contains(".")) {
            val serializedNameComponents = elementName.split(".")
            var jsonElement: JsonElement? = null
            serializedNameComponents.forEachIndexed { index, serializedNameComponent ->
                if (jsonElement == null) {
                    jsonElement = jsonObject[serializedNameComponent] ?: JsonNull
                } else {
                    if (jsonElement is JsonNull) {
                        return JsonNull
                    } else if (jsonElement is JsonObject && index == serializedNameComponents.lastIndex) {
                        jsonElement =
                            jsonElement?.jsonObject?.get(serializedNameComponent) ?: JsonNull
                        return parse(jsonElement, descriptor)
                    } else if (jsonElement is JsonObject) {
                        jsonElement = jsonElement?.jsonObject?.get(serializedNameComponent)
                            ?: JsonNull
                    } else {
                        jsonElement ?: JsonNull
                    }
                }
            }
            return JsonNull
        } else if (jsonObject.containsKey(elementName)) {
            return parse(jsonObject[elementName], descriptor)
        } else {
            return JsonNull
        }
    }

    @ExperimentalSerializationApi
    fun parse(
        jsonElement: JsonElement?,
        descriptor: SerialDescriptor
    ): JsonElement {
        return when (jsonElement) {
            is JsonObject -> {
                val jsonObjectValue = jsonElement.jsonObject
                parse(jsonObjectValue, descriptor)
            }
            is JsonArray -> {
                parse(jsonElement, descriptor)
            }
            is JsonPrimitive -> {
                jsonElement
            }
            else -> {
                jsonElement ?: JsonNull
            }
        }
    }

    @ExperimentalSerializationApi
    fun parse(
        jsonArray: JsonArray,
        descriptor: SerialDescriptor
    ): JsonElement {
        if(jsonArray.isEmpty()) return jsonArray
        if (descriptor.kind == StructureKind.LIST) {
            // The descriptor represents a List of data classes
            // Implement parsing logic for List of data classes

            val data = jsonArray.mapIndexed { index, jsonElement ->
                val childDescriptor = descriptor.getElementDescriptor(index)
                parse(jsonElement, childDescriptor)
            }

            return JsonArray(data)

        } else if (descriptor.kind == StructureKind.CLASS && descriptor.elementsCount == 0) {
            // The descriptor represents a Pure JsonArray
            // Implement parsing logic for Pure JsonArray
            return jsonArray
        } else {
            throw NetworkErrorMapper.mapToAppException(
                "Unsupported SerialDescriptor kind: ${descriptor.kind}",
                -1
            )
        }
    }
}

val DummyObject = JsonObject(mapOf("key" to JsonPrimitive("value")))


fun JsonObject.keyExists(elementName: String): Boolean {
    var exists = false
    var breakLoop = false
    val searchMap = elementName.split(".")
    var currentObject = this

    searchMap.forEachIndexed loop@{ index, item ->
        if (!breakLoop) {
            if (index == searchMap.lastIndex && currentObject.containsKey(item)) {
                exists = true
                breakLoop = true
            }

            val checkedObject = currentObject.get(item)
            if (checkedObject is JsonObject) {
                currentObject = checkedObject
            } else {
                breakLoop = true
            }
        }
    }

    return exists
}