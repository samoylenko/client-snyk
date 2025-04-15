package dev.samoylenko.client.snyk.model.request

import kotlin.Boolean
import kotlinx.serialization.Serializable

@Serializable
data class AggregatedIssuesRequest(
    val includeDescription: Boolean? = null,
    val includeIntroducedThrough: Boolean? = null,
    val filters: Filters? = null,
)
