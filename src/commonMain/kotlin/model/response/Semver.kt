package dev.samoylenko.client.snyk.model.response

import dev.samoylenko.client.snyk.model.response.serialization.FlexibleStringListSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Semver(
    @Serializable(with = FlexibleStringListSerializer::class)
    val vulnerable: List<String>? = emptyList(),
    val unaffected: String? = null,
)
