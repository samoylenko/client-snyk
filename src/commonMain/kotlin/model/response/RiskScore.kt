package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RiskScore(
    val model: String,
    @SerialName("updated_at") val updatedAt: Instant? = null,
    val value: Int,
)
