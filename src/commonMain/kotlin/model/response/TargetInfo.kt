package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
public data class TargetInfo(
    override val id: String,
    override val type: String,
    val attributes: TargetAttributes,
) : InfoElement
