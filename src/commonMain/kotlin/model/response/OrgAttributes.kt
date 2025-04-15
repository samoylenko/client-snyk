package dev.samoylenko.client.snyk.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrgAttributes(
    @SerialName("group_id") val groupId: String? = null,
    @SerialName("is_personal") val isPersonal: Boolean,
    val name: String,
    val slug: String,
)
