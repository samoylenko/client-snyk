package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AggregatedIssuesResponse(
    val issues: List<Issue> = emptyList(),
)
