package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class AggregatedIssuesResponse(
    val issues: List<Issue> = emptyList(),
)
