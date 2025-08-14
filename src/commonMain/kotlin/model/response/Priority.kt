package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class Priority(
    val score: Double? = null,
    val factors: List<JsonElement>? = emptyList(),
)
