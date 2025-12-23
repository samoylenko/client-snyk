package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class IgnoreIssueResponse(
    override val ignoredBy: IgnoredBy,
    override val reason: String,
    override val reasonType: String,
    override val created: Instant,
    override val disregardIfFixable: Boolean,
    override val expires: Instant? = null,
) : IgnoreReason
