package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class Identifiers(
    val CVE: List<String>? = emptyList(),
    val CWE: List<String>? = emptyList(),
    val OSVDB: List<String>? = emptyList(),
)
