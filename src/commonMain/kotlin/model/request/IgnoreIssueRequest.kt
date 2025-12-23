package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
public data class IgnoreIssueRequest(
    val reason: String,
    val reasonType: IgnoreReasonType,
    @EncodeDefault
    val disregardIfFixable: Boolean = false,
    val ignorePath: String = "*",
    val expires: String? = null,
)
