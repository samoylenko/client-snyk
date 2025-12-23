package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class Coordinates(
    @SerialName("created_at") val createdAt: Instant? = null,
    @SerialName("is_fixable_manually") val isFixableManually: Boolean? = null,
    @SerialName("is_fixable_snyk") val isFixableSnyk: Boolean? = null,
    @SerialName("is_fixable_upstream") val isFixableUpstream: Boolean? = null,
    @SerialName("is_patchable") val isPatchable: Boolean? = null,
    @SerialName("is_pinnable") val isPinnable: Boolean? = null,
    @SerialName("is_upgradeable") val isUpgradeable: Boolean? = null,
    @SerialName("last_introduced_at") val lastIntroducedAt: Instant? = null,
    @SerialName("last_resolved_at") val lastResolvedAt: Instant? = null,
    @SerialName("last_resolved_details") val lastResolvedDetails: String? = null,
    val reachability: Reachability? = null,
    val remedies: Collection<Remedy>? = null,
    val representations: JsonElement? = null, // Skipped
    val state: State? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
