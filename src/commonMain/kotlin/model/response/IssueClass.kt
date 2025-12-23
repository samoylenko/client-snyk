package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class IssueClass(
    val id: String,
    val source: String,
    val type: SourceType,
    val url: String? = null,
)
