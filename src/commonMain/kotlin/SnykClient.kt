package dev.samoylenko.client.snyk

import dev.samoylenko.client.snyk.model.request.AggregatedIssuesRequest
import dev.samoylenko.client.snyk.model.response.*
import dev.samoylenko.util.platform.Extensions.readText
import dev.samoylenko.util.platform.PlatformUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * A basic Snyk REST API client that supports both v1 and new REST endpoints.
 *
 * @param snykToken Snyk API Token. If not provided, will attempt to get it from the local environment.
 * @param rateLimitDelay Delay between Snyk HTTP requests. A primitive rate limiting implementation.
 */
class SnykClient(
    snykToken: String? = null,
    private val rateLimitDelay: Duration = 1.seconds,
) : AutoCloseable {
    companion object {
        const val SNYK_API_VERSION = "2024-06-21"

        const val TOKEN_TYPE_DEFAULT = "token"
        const val TOKEN_TYPE_FALLBACK = "bearer"

        private val logger = KotlinLogging.logger {}

        private val defaultJson = Json {
            // In
            ignoreUnknownKeys = true
            isLenient = true
            // Out
            prettyPrint = true
        }

        /**
         * An attempt to get the currently used Snyk API token from the local environment
         *
         * @return token and its type ("token", "bearer")
         */
        fun attemptGetSnykToken(): Pair<String, String>? =
            PlatformUtils.getEnv("SNYK_TOKEN")?.let {
                logger.debug { "Detected an API token in SNYK_TOKEN environment variable" }
                Pair(it, TOKEN_TYPE_DEFAULT)
            }
                ?: PlatformUtils.getHomeDir()
                    ?.let { Path(it, ".config", "configstore", "snyk.json") }
                    ?.let { snykConfigPath ->
                        if (SystemFileSystem.exists(snykConfigPath)) {
                            runCatching {
                                val snykConfig = defaultJson.decodeFromString<JsonObject>(snykConfigPath.readText())
                                snykConfig["api"]?.jsonPrimitive?.content?.let { apiToken ->
                                    logger.debug { "Detected an API token in Snyk config file" }
                                    Pair(apiToken, TOKEN_TYPE_DEFAULT)
                                } ?: snykConfig["INTERNAL_OAUTH_TOKEN_STORAGE"]?.jsonPrimitive?.content
                                    ?.let { defaultJson.decodeFromString<JsonObject>(it) }
                                    ?.let { oauthStorage ->
                                        logger.debug { "Detected an OAUTH token in Snyk config file" }
                                        // TODO: Meaningful errors instead `!!`
                                        Pair(
                                            oauthStorage["access_token"]?.jsonPrimitive?.content!!,
                                            oauthStorage["token_type"]?.jsonPrimitive?.content!!,
                                        )
                                    }
                            }.onFailure {
                                logger.warn(throwable = it) { "Could not read api key value from Snyk config file" }
                            }.getOrNull()
                        } else null
                    }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun getSnykAuthHeaderValue(snykToken: String?): String =
        snykToken?.let {
            val isUUid = runCatching { Uuid.parse(snykToken) }.isSuccess
            if (isUUid) "$TOKEN_TYPE_DEFAULT $snykToken" else "$TOKEN_TYPE_FALLBACK $snykToken"
        }
            ?: attemptGetSnykToken()?.let { (token, type) ->
                "$type $token"
            } ?: throw Error("Could not find a Snyk token. Please provide a Snyk API token")

    /** Direct access to the HTTP Client instance configured to use Snyk API */
    val client = HttpClient {
        install(ContentNegotiation) { json(defaultJson) }
        install(Logging) { level = LogLevel.INFO } // TODO: allow user set logging level

        CurlUserAgent()
        defaultRequest {
            url("https://api.snyk.io")
            url.parameters.appendIfNameAbsent("version", SNYK_API_VERSION)
            header(HttpHeaders.ContentType, "application/vnd.api+json")
            header(HttpHeaders.Authorization, getSnykAuthHeaderValue(snykToken))
        }

        expectSuccess = true
    }

    override fun close() {
        client.close()
    }

    /**
     * Direct access to paged Snyk request function.
     *
     * @param urlString api endpoint
     * @param method HTTP method
     *
     * @return Collection of Snyk data
     */
    suspend fun pagedSnykRequest(
        urlString: String,
        method: HttpMethod = HttpMethod.Get,
    ): Collection<ResponseDataElement> {
        val result = mutableListOf<ResponseDataElement>()
        var nextPage: String? = urlString

        while (nextPage != null) {
            delay(rateLimitDelay)
            val responseBody: PagedResponse = client.request(nextPage) { this.method = method }.body()
            result.addAll(responseBody.data)
            nextPage = responseBody.links.next
        }

        return result
    }

    /**
     * Get all [orgs](https://apidocs.snyk.io/?version=2024-10-15#get-/orgs)
     */
    suspend fun getAllOrgs(): Collection<OrgInfo> =
        pagedSnykRequest("/rest/orgs").map {
            OrgInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes)
            )
        }

    /**
     * [Get all org projects](https://apidocs.snyk.io/?version=2024-10-15#get-/orgs/-org_id-/projects)
     */
    suspend fun getOrgProjects(orgId: String): Collection<ProjectInfo> =
        pagedSnykRequest("/rest/orgs/${orgId}/projects").map {
            ProjectInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
                meta = it.meta?.let { meta -> defaultJson.decodeFromJsonElement(meta) },
                relationships = it.relationships
            )
        }

    /**
     * [Delete project by project ID](https://apidocs.snyk.io/?version=2024-10-15#delete-/orgs/-org_id-/projects/-project_id-)
     *
     * @param orgId Org ID
     * @param projectId Project ID
     */
    suspend fun deleteProject(orgId: String, projectId: String) {
        client.delete("/rest/orgs/$orgId/projects/$projectId")
    }

    /**
     * [Get all org targets](https://apidocs.snyk.io/?version=2024-10-15#get-/orgs/-org_id-/targets)
     *
     * @param orgId Org ID
     */
    suspend fun getOrgTargets(
        orgId: String,
        excludeEmpty: Boolean = true
    ): Collection<TargetInfo> =
        pagedSnykRequest("/rest/orgs/${orgId}/targets?exclude_empty=${excludeEmpty}").map {
            TargetInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
            )
        }

    /**
     * [V1: Get Aggregated Project Issues](https://docs.snyk.io/snyk-api/reference/projects-v1#org-orgid-project-projectid-aggregated-issues)
     *
     * @param orgId Org ID
     * @param projectId Project ID
     * @param aggregatedIssuesRequest additional request config
     */
    suspend fun getProjectIssuesAggregated(
        orgId: String,
        projectId: String,
        aggregatedIssuesRequest: AggregatedIssuesRequest? = null
    ): Collection<Issue> =
        client
            .post("/v1/org/${orgId}/project/${projectId}/aggregated-issues") { setBody(aggregatedIssuesRequest) }
            .body<AggregatedIssuesResponse>()
            .issues

    /**
     * [V1: Get project Jira issues](https://docs.snyk.io/snyk-api/reference/jira-v1#org-orgid-project-projectid-jira-issues)
     *
     * @param orgId Org ID
     * @param projectId Project ID
     *
     * @return Map: Issue ID to Jira information
     */
    suspend fun getProjectJiraIssues(
        orgId: String,
        projectId: String,
    ): JiraIssuesResponse =
        client
            .get("/v1/org/${orgId}/project/${projectId}/jira-issues")
            .body()
}
