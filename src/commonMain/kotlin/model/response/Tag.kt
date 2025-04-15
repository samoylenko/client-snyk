package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val key: String,
    val value: String
)
