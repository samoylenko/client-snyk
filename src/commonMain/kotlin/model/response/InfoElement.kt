package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

sealed interface InfoElement {
    val id: String
    val type: String

    @Serializable
    data class InfoElementImpl(
        override val id: String,
        override val type: String
    ): InfoElement
}
