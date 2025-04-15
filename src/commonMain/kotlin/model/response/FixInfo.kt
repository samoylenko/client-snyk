package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FixInfo(
    val isUpgradable: Boolean? = null,
    val isPinnable: Boolean? = null,
    val isPatchable: Boolean? = null,
    val isFixable: Boolean? = null,
    val isPartiallyFixable: Boolean? = null,
    val nearestFixedInVersion: String? = null,
    val fixedIn: List<String>? = emptyList(),
)
