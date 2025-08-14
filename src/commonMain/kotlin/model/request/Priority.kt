package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.Serializable

@Serializable
public data class Priority(
    val score: Score? = null,
)
