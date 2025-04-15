package dev.samoylenko.client.snyk.model.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ProjectAttributes(
    val name: String,
    val type: String,
    @SerialName("target_file") val targetFile: String,
    @SerialName("target_reference") val targetReference: String,
    val origin: String,
    val created: Instant,
    val status: String,
    @SerialName("business_criticality") val businessCriticality: JsonElement, // TODO
    val environment: JsonElement, // TODO
    val lifecycle: JsonElement, // TODO
    val tags: Collection<Tag>,
    @SerialName("read_only") val reaOnly: Boolean,
    val settings: JsonElement, // TODO
)
