package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class IssueType {
    @SerialName("package_vulnerability")
    PACKAGE_VULNERABILITY,

    @SerialName("license")
    LICENSE,

    @SerialName("cloud")
    CLOUD,

    @SerialName("code")
    CODE,

    @SerialName("custom")
    CUSTOM,

    @SerialName("config")
    CONFIG,
}
