package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class ProjectInfo(
    override val id: String,
    /** See https://docs.snyk.io/snyk-api/api-endpoints-index-and-tips/project-type-responses-from-the-api */
    override val type: String,
    val attributes: ProjectAttributes,
    val meta: ProjectMetadata?,
    val relationships: Map<String, Relationship?>?,
) : InfoElement
