package dev.samoylenko.client.snyk.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class IgnoreReasonType {
    @SerialName("not-vulnerable")
    NOT_VULNERABLE,

    @SerialName("temporary-ignore")
    TEMPORARY_IGNORE,

    @SerialName("wont-fix")
    WONT_FIX,
}
