package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class JiraIssueInfo(
    val id: String,
    val key: String
)
