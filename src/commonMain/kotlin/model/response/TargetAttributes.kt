package dev.samoylenko.client.snyk.model.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TargetAttributes(
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("display_name") val displayName: String,
    @SerialName("is_private") val isPrivate: Boolean,
    val url: String?,
)
