package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Semver(
    val vulnerable: List<String>? = emptyList(),
    val unaffected: String? = null,
)
