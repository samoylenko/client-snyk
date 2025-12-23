package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class State {
    @SerialName("open")
    OPEN,

    @SerialName("resolved")
    RESOLVED,
}
