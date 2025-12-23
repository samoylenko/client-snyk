package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class SourceType {
    @SerialName("rule-category")
    RULE_CATEGORY,

    @SerialName("compliance")
    COMPLIANCE,

    @SerialName("weakness")
    WEAKNESS,
}
