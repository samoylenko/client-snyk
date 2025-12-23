package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class Reachability {
    @SerialName("function")
    FUNCTION,

    @SerialName("package")
    PACKAGE,

    @SerialName("no-info")
    NO_INFO,

    @SerialName("not-applicable")
    NOT_APPLICABLE,
}
