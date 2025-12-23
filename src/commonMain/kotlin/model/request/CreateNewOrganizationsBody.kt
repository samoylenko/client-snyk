package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.Serializable

@Serializable
public data class CreateNewOrganizationsBody(
    val name: String,
    val groupId: String? = null,
    val sourceOrgId: String? = null,
)
