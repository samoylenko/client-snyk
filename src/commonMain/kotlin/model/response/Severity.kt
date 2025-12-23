package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Severity(
    val level: SeverityLevel,
    @SerialName("modification_time") val modificationTime: Instant,
    val score: Float,
    val source: String,
    val vector: String,
    val version: String,
)
