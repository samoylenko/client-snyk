package dev.samoylenko.client.snyk.model.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class CreateANewOrganizationResponse(
    val id: String,
    val name: String,
    val slug: String,
    val url: String,
    val created: Instant,
    val group: Group,
)
