package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

public sealed interface InfoElement {
    public val id: String
    public val type: String

    @Serializable
    public data class InfoElementImpl(
        override val id: String,
        override val type: String
    ): InfoElement
}
