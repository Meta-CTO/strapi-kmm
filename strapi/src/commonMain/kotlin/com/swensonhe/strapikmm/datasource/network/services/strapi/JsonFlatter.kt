package com.swensonhe.strapikmm.datasource.network.services.strapi

import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import com.swensonhe.strapikmm.util.Logger
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

object JsonFlatter {

    /**
     * Parses a [JsonElement] into a target data class of type [T], using inline reified generics.
     *
     * @param T The target data class type to which the [JsonElement] will be deserialized.
     * @param jsonElement The JSON data to be deserialized.
     * @return A deserialized [JsonElement] representing the target data class of type [T].
     *
     * @throws IllegalStateException if the input JSON is malformed and doesn't represent a valid object or array.
     *
     * This function uses Kotlin Serialization to convert a JSON representation into a data class
     * using the [serializer] function, which is called with the reified type [T].
     *
     * If [strapiNetworkLogLevel] is set to [NetworkLogLevel.ALL], it logs the input JSON string.
     *
     * The function iterates through the properties (elements) of the data class and processes them
     * based on JSON annotations, such as [JsonNames] and [SerialName], to match property names to JSON keys.
     *
     * For each property, it creates a mapping from the property name to its corresponding JSON value,
     * taking into account any JSON alias names specified in annotations.
     *
     * The resulting map is used to build a new [JsonObject] representing the deserialized data class.
     * For properties of type List or other generic collections, the function handles [JsonArray] as well.
     *
     * Usage Example:
     * ```kotlin
     * @Serializable
     * data class UserData(
     *     @SerialName("name")
     *     val name: String? = null,
     *     @SerialName("address.city")
     *     val city: String? = null,
     *     @SerialName("address.state")
     *     val state: String? = null
     * )
     * val jsonString = "{\"name\":\"John\",\"address\":{\"city\":\"New York\",\"state\":\"NY\"}}"
     * val jsonElement = Json.parseToJsonElement(jsonString)
     * val flatJson = flat<UserData>(jsonElement)
     * ```
     * @OptIn(ExperimentalSerializationApi::class)
     * @see JsonNames
     * @see SerialName
     * @see NetworkLogLevel
     * @see JsonElement
     * @see JsonNames
     * @see SerialName
     * @see serializer
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> flat(jsonElement: JsonElement): JsonElement {
        // Check if network log level is set to ALL
        if (strapiNetworkLogLevel == NetworkLogLevel.ALL) {
            // Log the original JSON as a string
            Logger("").log(jsonElement.toString())
        }

        // Get the descriptor for the specified type
        val descriptor = serializer(typeOf<T>()).descriptor
        // Get the element names from the descriptor
        val elementNames = descriptor.elementNames

        return when (jsonElement) {
            is JsonObject -> {
                // Create a mutable map to store flattened JSON elements
                val map = mutableMapOf<String, JsonElement>()

                // Iterate over the element names in the descriptor
                elementNames.forEachIndexed { index, elementName ->
                    // Get the descriptor for the element at the current index
                    val childDescriptor = descriptor.getElementDescriptor(index)

                    val jsonNames = mutableListOf<String>()
                    // Get the annotations for the element at the current index, This is used to determine the JSON key.
                    val annotations = descriptor.getElementAnnotations(index)
                        // Filter the annotations to get only the JsonNames and SerialName annotations to make sure we get the correct JSON key.
                        .filter { it is JsonNames || it is SerialName }

                    // Iterate through the annotations to get the JSON key.
                    annotations.forEach { annotation ->
                        // If the annotation is a SerialName, add the value to the jsonNames list.
                        if (annotation is SerialName) {
                            // Add the value to the jsonNames list
                            jsonNames.add(annotation.value)
                            // If the annotation is a JsonNames, add the values to the jsonNames list.
                        } else if (annotation is JsonNames) {
                            // Sort the values by the number of '.' in the value to make sure we get the correct JSON key.
                            jsonNames.addAll(
                                annotation.names.sortedBy { it.split(".").size }
                            )
                        }
                    }

                    // If the jsonNames list is empty, add the elementName to the list.
                    if (jsonNames.isEmpty()) {
                        jsonNames.add(elementName)
                    }

                    // Default value if the item not presented in the json
                    map[elementName] = JsonNull

                    // Iterate through the jsonNames list to get the JSON key.
                    jsonNames.forEach { element ->
                        // Parse the JSON element based on the JSON key and the descriptor.
                        val value = parse(element, jsonElement, childDescriptor)
                        // If the value is not a DummyJsonObject or null, add it to the map.
                        if (value != DummyJsonObject && value != null) {
                            map[elementName] = value
                        }
                    }
                }

                // Return the map as a JsonObject.
                JsonObject(map)
            }
            // If the JSON element is an array, parse it as a JSON array.
            is JsonArray -> {
                // Parse each element in the JSON array
                val jsonElements = jsonElement.mapIndexed { index, element ->
                    // Get the descriptor for the element at the current index
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    // Parse the JSON element based on the descriptor.
                    parse(element, childDescriptor)
                }

                // Return the parsed JSON array
                JsonArray(jsonElements)
            }
            else -> {
                // If the JSON element is not an object or array, throw an exception indicating the JSON is malformed.
                throw IllegalStateException("Malformed JSON passed to parser, expected object or array but got $jsonElement")
            }
        }
    }

    /**
     * Parses a [JsonObject] based on the provided [SerialDescriptor].
     *
     * This function recursively processes a JSON object and its nested elements to create a structured
     * [JsonObject] that matches the provided [SerialDescriptor].
     *
     * @param json The JSON object to be parsed.
     * @param descriptor The [SerialDescriptor] representing the expected structure.
     * @return The parsed [JsonObject] matching the provided [SerialDescriptor].
     */
    @ExperimentalSerializationApi
    fun parse(json: JsonObject, descriptor: SerialDescriptor): JsonObject {
        // If the JSON object is empty, return it as is.
        if(json.isEmpty()) {
            return json
        }

        // If the descriptor represents a sealed class or interface, return the JSON as is.
        if (descriptor.kind == PolymorphicKind.SEALED) {
            return json
        }

        val map = mutableMapOf<String, JsonElement>()
        val elementNames = descriptor.elementNames

        // Iterate through the element names in the descriptor to process the JSON object.
        elementNames.forEachIndexed { index, elementName ->
            // Get the descriptor for the element at the current index.
            val childDescriptor = descriptor.getElementDescriptor(index)
            val jsonNames = mutableListOf<String>()
            // Get the annotations for the element at the current index, This is used to determine the JSON key.
            val annotations = descriptor.getElementAnnotations(index)
                // Filter the annotations to get only the JsonNames and SerialName annotations to make sure we get the correct JSON key.
                .filter { it is JsonNames || it is SerialName }

            // Iterate through the annotations to get the JSON key.
            annotations.forEach { annotation ->
                // If the annotation is a SerialName, add the value to the jsonNames list.
                if (annotation is SerialName) {
                    jsonNames.add(annotation.value)
                    // If the annotation is a JsonNames, add the values to the jsonNames list.
                } else if (annotation is JsonNames) {
                    // Sort the values by the number of '.' in the value to make sure we get the correct JSON key.
                    jsonNames.addAll(
                        annotation.names.sortedBy { it.split(".").size }
                    )
                }
            }

            // If the jsonNames list is empty, add the elementName to the list.
            if (jsonNames.isEmpty()) {
                jsonNames.add(elementName)
            }

            // Add the elementName to the map with a default value of JsonNull.
            // Default value if the item not presented in the json
            map[elementName] = JsonNull

            // Iterate through the jsonNames list to get the JSON key.
            jsonNames.forEach { element ->
                // Parse the JSON element based on the JSON key and the descriptor.
                val value = parse(element, json, childDescriptor)
                // If the value is not a DummyJsonObject or null, add it to the map.
                if (value != DummyJsonObject && value != null) {
                    map[elementName] = value
                }
            }
        }

        // Return the map as a JsonObject.
        return JsonObject(map)
    }

    /**
     * Parses a nested JSON element within a [JsonObject] based on the provided [SerialDescriptor].
     *
     * This function recursively processes a nested JSON element and its descendants to create a structured
     * [JsonElement] that matches the provided [SerialDescriptor].
     *
     * @param elementName The dot-separated path to the nested element.
     * @param jsonObject The JSON object containing the nested element.
     * @param descriptor The [SerialDescriptor] representing the expected structure.
     * @return The parsed [JsonElement] matching the provided [SerialDescriptor].
     */
    @ExperimentalSerializationApi
    fun parse(
        elementName: String,
        jsonObject: JsonObject,
        descriptor: SerialDescriptor
    ): JsonElement {
        // Check if the element path / name exist within jsonObject
        if (jsonObject.containsKeyPath(elementName).not()) {
            // If the element path / name does not exist, return a dummy JsonObject
            return DummyJsonObject
        }

        if (elementName.contains(".")) {
            // If the elementName contains '.', it's a nested key path and we need to parse it recursively.
            val serializedNameComponents = elementName.split(".")
            var jsonElement: JsonElement? = null
            // Traverse the nested key path to find the nested element.
            serializedNameComponents.forEachIndexed { index, serializedNameComponent ->
                // if the jsonElement is null, it means we are at the root of the nested key path.
                if (jsonElement == null) {
                    // Get the nested element from the jsonObject based on the serializedNameComponent (key) at the current index or return JsonNull.
                    jsonElement = jsonObject[serializedNameComponent] ?: JsonNull
                } else {
                    // If the jsonElement is not null, it means we are at a nested level of the key path.
                    if (jsonElement is JsonNull) {
                        // If the jsonElement is JsonNull, return it.
                        return JsonNull
                        // If the jsonElement is a JsonObject and we are at the last index of the serializedNameComponents, parse the jsonElement based on the descriptor.
                    } else if (jsonElement is JsonObject && index == serializedNameComponents.lastIndex) {
                        // Get the descriptor for the nested element.
                        jsonElement =
                            jsonElement?.jsonObject?.get(serializedNameComponent) ?: JsonNull
                        // Parse the nested element based on the descriptor.
                        return parse(jsonElement, descriptor)
                        // If the jsonElement is a JsonObject, get the nested element based on the serializedNameComponent (key) at the current index or return JsonNull.
                    } else if (jsonElement is JsonObject) {
                        // Get the nested element from the jsonElement based on the serializedNameComponent (key) at the current index or return JsonNull.
                        jsonElement = jsonElement?.jsonObject?.get(serializedNameComponent)
                            ?: JsonNull
                    } else {
                        // If the jsonElement is not a JsonObject, return it as is or return JsonNull.
                        jsonElement ?: JsonNull
                    }
                }
            }
            // If the jsonElement is null, return JsonNull.
            return JsonNull
            // If the elementName does not contain '.', it's a top-level key and we can parse it directly.
        } else if (jsonObject.containsKey(elementName)) {
            // Get the descriptor for the element based on the elementName and parse the element based on the descriptor.
            return parse(jsonObject[elementName], descriptor)
        } else {
            // If the elementName does not exist within the jsonObject, return JsonNull.
            return JsonNull
        }
    }

    /**
     * Parses a [JsonElement] based on the provided [SerialDescriptor].
     *
     * This function recursively processes a JSON element and its nested elements to create a structured
     * [JsonElement] that matches the provided [SerialDescriptor].
     *
     * @param jsonElement The JSON element to be parsed.
     * @param descriptor The [SerialDescriptor] representing the expected structure.
     * @return The parsed [JsonElement] matching the provided [SerialDescriptor].
     */
    @ExperimentalSerializationApi
    fun parse(jsonElement: JsonElement?, descriptor: SerialDescriptor): JsonElement {
        return when (jsonElement) {
            is JsonObject -> {
                // If the JSON element is an object, parse it as a JSON object.
                val jsonObjectValue = jsonElement.jsonObject
                parse(jsonObjectValue, descriptor)
            }
            is JsonArray -> {
                // If the JSON element is an array, parse it as a JSON array.
                parse(jsonElement, descriptor)
            }
            is JsonPrimitive -> {
                // If the JSON element is a primitive value, return it as is.
                jsonElement
            }
            else -> {
                // If the JSON element is null, return it as is or return the current element.
                jsonElement ?: JsonNull
            }
        }
    }

    /**
     * Parses a JSON array represented as a [JsonArray] based on the provided [SerialDescriptor].
     *
     * This function recursively processes a JSON array and its elements to create a structured
     * [JsonElement] that matches the provided [SerialDescriptor].
     *
     * @param jsonArray The JSON array to be parsed.
     * @param descriptor The [SerialDescriptor] representing the expected structure.
     * @return The parsed [JsonElement] matching the provided [SerialDescriptor].
     */
    @ExperimentalSerializationApi
    fun parse(jsonArray: JsonArray, descriptor: SerialDescriptor): JsonElement {
        // If the JSON array is empty, return it as is.
        if (jsonArray.isEmpty()) {
            return jsonArray
        }

        // Check the kind of the descriptor to determine the parsing logic.
        when (descriptor.kind) {
            StructureKind.LIST -> {
                // The descriptor represents a List of data classes.
                // Implement parsing logic for List of data classes.
                val data = jsonArray.mapIndexed { index, jsonElement ->
                    // Get the descriptor for the data class.
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    // Parse the JSON element based on the descriptor.
                    parse(jsonElement, childDescriptor)
                }

                return JsonArray(data)
            }
            StructureKind.CLASS -> {
                if (descriptor.elementsCount == 0) {
                    // The descriptor represents a Pure JsonArray.
                    // Implement parsing logic for Pure JsonArray.
                    return jsonArray
                } else {
                    // Unsupported SerialDescriptor kind for this use case.
                    throw IllegalArgumentException("Unsupported SerialDescriptor kind: ${descriptor.kind}")
                }
            }
            else -> {
                // Unsupported SerialDescriptor kind for this use case.
                throw IllegalArgumentException("Unsupported SerialDescriptor kind: ${descriptor.kind}")
            }
        }
    }

}

// A dummy JsonObject to be returned when a key path does not exist within a JsonObject.
val DummyJsonObject = JsonObject(mapOf("key" to JsonPrimitive("value")))

/**
 * Checks if a key path exists within a JsonObject.
 *
 * @param keyPath The dot-separated path to the nested key.
 * @return `true` if the key exists; `false` otherwise.
 */
fun JsonObject.containsKeyPath(keyPath: String): Boolean {
    val keySteps = keyPath.split(".")
    var currentObject: JsonObject? = this

    for (keyStep in keySteps) {
        val element = currentObject?.get(keyStep)
        // If the current element is a JsonObject, continue checking the next key step.
        if (element is JsonObject) {
            currentObject = element
        } else {
            // If the current element is not a JsonObject (e.g., string, boolean, array),
            // return `true` if the element is not null, indicating the key exists at this point.
            return element != null
        }
    }

    // If all key steps have been successfully traversed, return `true` to indicate the key exists.
    return true
}
