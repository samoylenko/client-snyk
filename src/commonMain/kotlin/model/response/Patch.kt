package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant

@Serializable
public data class Patch(
    val id: String,
    val urls: Collection<String>,
    val version: String,
    val comments: Collection<JsonObject>,
    val modificationTime: Instant,
)
