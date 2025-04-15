package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.Serializable

@Serializable
data class IssueData(
    val id: String,
    val title: String,
    val severity: String,
    val originalSeverity: String? = null,
    val url: String,
    val description: String? = null,
    val identifiers: Identifiers,
    val credit: List<String> = emptyList(),
    val exploitMaturity: String,
    val semver: Semver,
    val publicationTime: String,
    val disclosureTime: String? = null,
    val CVSSv3: String? = null,
    val cvssScore: Double? = null,
    val language: String,
    val patches: List<String> = emptyList(),
    val nearestFixedInVersion: String,
    val path: String? = null,
    val violatedPolicyPublicId: String? = null,
    val isMaliciousPackage: Boolean,
)
