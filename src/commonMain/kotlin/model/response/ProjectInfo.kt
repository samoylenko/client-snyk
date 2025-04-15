package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ProjectInfo(
    override val id: String,
    override val type: String,
    val attributes: ProjectAttributes,
    // TODO: are these nullable?
    val meta: ProjectMetadata?,
    val relationships: Map<String, Relationship?>?,
) : InfoElement
