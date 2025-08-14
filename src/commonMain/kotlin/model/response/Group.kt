package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class Group(
    val name: String,
    val id: String,
)
