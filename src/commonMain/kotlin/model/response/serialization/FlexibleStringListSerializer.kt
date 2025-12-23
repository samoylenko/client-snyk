package dev.samoylenko.client.snyk.model.response.serialization

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonPrimitive

public object FlexibleStringListSerializer :
    JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        when (element) {
            is JsonPrimitive -> if (element.jsonPrimitive.isString && element.jsonPrimitive.content.isNotEmpty())
                JsonArray(listOf(element))
            else
                JsonArray(emptyList())

            else -> element
        }
}
