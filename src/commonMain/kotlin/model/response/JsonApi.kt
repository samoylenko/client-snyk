package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class JsonApi(
    val version: String
)
