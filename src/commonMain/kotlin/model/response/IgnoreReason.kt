package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant

public interface IgnoreReason {
    public val reason: String
    public val created: Instant
    public val ignoredBy: IgnoredBy
    public val reasonType: String
    public val disregardIfFixable: Boolean
    public val expires: Instant?
}
