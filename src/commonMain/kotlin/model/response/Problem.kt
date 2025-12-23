package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Problem(
    @SerialName("disclosed_at") val disclosedAt: Instant? = null,
    @SerialName("discovered_at") val discoveredAt: Instant? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
    val id: String,
    val source: String,
    val type: ProblemType,
    val url: String? = null,
)
