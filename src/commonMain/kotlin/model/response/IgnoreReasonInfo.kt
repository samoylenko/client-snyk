package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class IgnoreReasonInfo(
    val path: Collection<String>,
    val source: String,
    override val reason: String,
    override val created: Instant,
    override val ignoredBy: IgnoredBy,
    override val reasonType: String,
    override val disregardIfFixable: Boolean,
    override val expires: Instant? = null,
) : IgnoreReason
