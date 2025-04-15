package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.Serializable

@Serializable
data class Filters(
    val severities: List<String>? = emptyList(),
    val exploitMaturity: List<String>? = emptyList(),
    val types: List<String>? = emptyList(),
    val ignored: Boolean? = null,
    val patched: Boolean? = null,
    val priority: Priority? = null,
)
