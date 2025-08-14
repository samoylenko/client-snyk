package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
public data class ProjectMetadata(
    @SerialName("cli_monitored_at") val cliMonitoredAt: Instant? = null,
)
