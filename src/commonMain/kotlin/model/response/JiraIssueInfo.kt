package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class JiraIssueInfo(
    val id: String,
    val key: String
)
