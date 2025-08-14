package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class PagedResponseLinks(
    val self: String? = null,
    val first: String? = null,
    val last: String? = null,
    val prev: String? = null,
    val next: String? = null,
)
