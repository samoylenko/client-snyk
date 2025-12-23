package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
public data class Remedy(
    @SerialName("correlation_id") val correlationId: String? = null,
    val description: String? = null,
    val meta: JsonObject, // Skipped
    val type: String,
)
