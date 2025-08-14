package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

public typealias JiraIssuesResponse = @Serializable Map<String, Collection<JiraIssuesResponseElement>>
