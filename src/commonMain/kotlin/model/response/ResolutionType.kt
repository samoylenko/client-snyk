package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class ResolutionType {
    @SerialName("disappeared")
    DISAPPEARED,

    @SerialName("fixed")
    FIXED,
}
