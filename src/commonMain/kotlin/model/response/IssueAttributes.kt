package dev.samoylenko.client.snyk.model.response

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class IssueAttributes(
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("effective_severity_level") val effectiveSeverityLevel: SeverityLevel,
    val ignored: Boolean,
    val key: String,
    val status: State,
    val title: String,
    val type: IssueType,
    @SerialName("updated_at") val updatedAt: Instant,
    val classes: Collection<IssueClass>? = null,
    val coordinates: Collection<Coordinates>? = null,
    val description: String? = null,
    @SerialName("exploit_details") val exploitDetails: ExploitDetails? = null,
    @SerialName("key_asset") val keyAsset: String? = null,
    val problems: Collection<Problem>? = null,
    val resolution: Resolution? = null,
    val risk: Risk? = null,
    val severities: Collection<Severity>? = null,
    val tool: String? = null,
)
