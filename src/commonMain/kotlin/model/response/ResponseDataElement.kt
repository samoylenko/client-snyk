package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class ResponseDataElement(
    override val id: String,
    override val type: String,
    val attributes: JsonElement,
    val meta: JsonElement? = null,
    val relationships: Map<String, Relationship?>? = null,
) : InfoElement
