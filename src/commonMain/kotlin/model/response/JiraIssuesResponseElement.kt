package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class JiraIssuesResponseElement(
    val jiraIssue: JiraIssueInfo
)
