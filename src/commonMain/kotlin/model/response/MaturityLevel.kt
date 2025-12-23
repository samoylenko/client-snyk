package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class MaturityLevel(
    val format: String,
    val level: String,
)
