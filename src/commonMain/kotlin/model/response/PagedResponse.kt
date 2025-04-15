package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse(
    val jsonapi: JsonApi,
    val links: PagedResponseLinks,
    val data: Collection<ResponseDataElement>,
)
