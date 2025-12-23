package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Resolution(
    val details: String? = null,
    @SerialName("resolved_at") val resolvedAt: Instant,
    val type: ResolutionType,
)
