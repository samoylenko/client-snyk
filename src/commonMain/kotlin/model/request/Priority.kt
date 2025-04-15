package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.Serializable

@Serializable
data class Priority(
    val score: Score? = null,
)
