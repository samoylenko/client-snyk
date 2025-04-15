package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class OrgInfo(
    override val id: String,
    override val type: String,
    val attributes: OrgAttributes,
) : InfoElement
