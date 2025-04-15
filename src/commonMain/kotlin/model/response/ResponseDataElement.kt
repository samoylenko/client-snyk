package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResponseDataElement(
    override val id: String,
    override val type: String,
    val attributes: JsonElement,
    val meta: JsonElement? = null,
    val relationships: Map<String, Relationship?>? = null,
) : InfoElement

@Serializable
data class Relationship(
    val data: InfoElement.InfoElementImpl,
    val links: Map<String, String>? = null,
)
