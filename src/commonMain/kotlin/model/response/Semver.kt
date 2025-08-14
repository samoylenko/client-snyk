package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class Semver(
    val vulnerable: List<String>? = emptyList(),
    val unaffected: String? = null,
)
