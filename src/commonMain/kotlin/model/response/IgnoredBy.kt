package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class IgnoredBy(
    val id: String,
    val name: String,
    val email: String,
)
