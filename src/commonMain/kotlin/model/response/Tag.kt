package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class Tag(
    val key: String,
    val value: String
)
