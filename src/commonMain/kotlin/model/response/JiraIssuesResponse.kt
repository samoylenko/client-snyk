package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

typealias JiraIssuesResponse = @Serializable Map<String, Collection<JiraIssuesResponseElement>>
