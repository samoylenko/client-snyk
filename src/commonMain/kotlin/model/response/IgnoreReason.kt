package dev.samoylenko.client.snyk.model.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class IgnoreReason(
    val path: Collection<String>,
    val reason: String,
    val source: String,
    val created: Instant,
    val ignoredBy: IgnoredBy,
    val reasonType: String,
    val disregardIfFixable: Boolean
)
