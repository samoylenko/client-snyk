package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val min: Double? = null,
    val max: Double? = null,
)
