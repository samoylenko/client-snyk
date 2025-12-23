package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class Risk(
    val factors: JsonElement, // Skipped
    val score: RiskScore? = null,
)
