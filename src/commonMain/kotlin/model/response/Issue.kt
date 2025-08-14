package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class Issue(
    val id: String,
    val issueType: String,
    val pkgName: String,
    val pkgVersions: List<String> = emptyList(),
    val issueData: IssueData,
    val introducedThrough: List<JsonElement>? = emptyList(),
    val isPatched: Boolean,
    val isIgnored: Boolean,
    val ignoreReasons: List<IgnoreReason>? = emptyList(),
    val fixInfo: FixInfo? = null,
    val priority: Priority? = null,
    val links: PagedResponseLinks? = null,
)
