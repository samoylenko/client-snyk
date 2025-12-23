package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class SnykIssue(
    override val id: String,
    override val type: String,
    val attributes: IssueAttributes,
    val relationships: Map<String, Relationship?>?,
) : InfoElement
