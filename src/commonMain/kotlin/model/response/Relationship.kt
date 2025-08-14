package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class Relationship(
    val data: InfoElement.InfoElementImpl,
    val links: Map<String, String>? = null,
)
