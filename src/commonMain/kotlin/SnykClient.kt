package dev.samoylenko.client.snyk

import dev.samoylenko.client.snyk.model.request.AggregatedIssuesRequest
import dev.samoylenko.client.snyk.model.request.CreateNewOrganizationsBody
import dev.samoylenko.client.snyk.model.request.IgnoreIssueRequest
import dev.samoylenko.client.snyk.model.request.IgnoreReasonType
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

/**
 * A basic Snyk REST API client that supports both v1 and new REST endpoints.
 *
 * @param snykToken Snyk API Token. If not provided, will attempt to get it from the local environment.
 * @param rateLimitDelay Delay between Snyk HTTP requests. A primitive rate limiting implementation.
 */
public class SnykClient(
    snykToken: String? = null,
    private val rateLimitDelay: Duration = 50.milliseconds,
) : AutoCloseable {
    private companion object {
        const val SNYK_API_VERSION = "2025-11-05"

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
        private fun attemptGetSnykToken(): Pair<String, String>? =
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

    private val snykAuthHeader: String = getSnykAuthHeaderValue(snykToken)

    private fun getSnykAuthHeaderValue(snykToken: String?): String =
        snykToken?.let {
            val isUUid = runCatching { Uuid.parse(snykToken) }.isSuccess
            if (isUUid) "$TOKEN_TYPE_DEFAULT $snykToken" else "$TOKEN_TYPE_FALLBACK $snykToken"
        } ?: attemptGetSnykToken()?.let { (token, type) ->
            "$type $token"
        } ?: throw Error("Could not find a Snyk token")

    /** Direct access to the HTTP Client instance configured to use Snyk API */
    public val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(defaultJson) }
        install(Logging) { level = LogLevel.INFO } // TODO: allow user set logging level
        install(HttpRequestRetry) {
            retryOnException(maxRetries = 5, retryOnTimeout = true) // TODO: param
            exponentialDelay()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000 // TODO: Param
        }

        CurlUserAgent()
        defaultRequest {
            url("https://api.snyk.io")
            url.parameters.appendIfNameAbsent("version", SNYK_API_VERSION)
            header(HttpHeaders.ContentType, "application/vnd.api+json")
            header(HttpHeaders.Authorization, snykAuthHeader)
        }

        expectSuccess = true
    }

    override fun close() {
        client.close()
    }

    /**
     * Direct access to the paged Snyk request function.
     *
     * @param urlString api endpoint
     * @param method HTTP method
     *
     * @return Collection of Snyk data
     */
    public suspend fun pagedSnykRequest(
        urlString: String,
        method: HttpMethod = HttpMethod.Get,
        urlParams: Map<String, String> = emptyMap(),
    ): Collection<ResponseDataElement> {
        val result = mutableListOf<ResponseDataElement>()
        var nextPage: String? = urlString

        while (nextPage != null) {
            delay(rateLimitDelay)
            val responseBody: PagedResponse =
                client.request(nextPage) {
                    this.method = method
                    urlParams.forEach { (k, v) -> this.url.parameters.appendIfNameAbsent(k, v) }
                }.body()
            result.addAll(responseBody.data)
            nextPage = responseBody.links.next
        }

        return result
    }

    /**
     * [List accessible organizations](https://docs.snyk.io/snyk-api/reference/orgs#get-orgs)
     */
    public suspend fun getOrgs(): Collection<OrgInfo> =
        pagedSnykRequest("/rest/orgs").map {
            OrgInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
            )
        }

    /**
     * [Get issues by org ID](https://docs.snyk.io/snyk-api/reference/issues#get-orgs-org_id-issues)
     */
    public suspend fun getOrgIssues(
        orgId: String,
        urlParams: Map<String, String> = emptyMap()
    ): Collection<SnykIssue> =
        pagedSnykRequest(
            urlString = "/rest/orgs/$orgId/issues",
            urlParams = urlParams,
        ).map {
            SnykIssue(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
                relationships = it.relationships,
            )
        }

    /**
     * [Get all org projects](https://docs.snyk.io/snyk-api/reference/projects#get-orgs-org_id-projects)
     */
    public suspend fun getOrgProjects(
        orgId: String,
        urlParams: Map<String, String> = emptyMap()
    ): Collection<ProjectInfo> =
        pagedSnykRequest(
            urlString = "/rest/orgs/$orgId/projects",
            urlParams = urlParams,
        ).map {
            ProjectInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
                meta = it.meta?.let { meta -> defaultJson.decodeFromJsonElement(meta) },
                relationships = it.relationships,
            )
        }

    /**
     * [Delete project](https://docs.snyk.io/snyk-api/reference/projects#delete-orgs-org_id-projects-project_id)
     *
     * @param orgId Org ID
     * @param projectId Project ID
     */
    public suspend fun deleteProject(orgId: String, projectId: String) {
        client.delete("/rest/orgs/$orgId/projects/$projectId")
    }

    /**
     * [Get org targets](https://docs.snyk.io/snyk-api/reference/targets#get-orgs-org_id-targets)
     *
     * @param orgId Org ID
     */
    public suspend fun getOrgTargets(
        orgId: String,
        urlParams: Map<String, String> = emptyMap(),
    ): Collection<TargetInfo> =
        pagedSnykRequest(
            urlString = "/rest/orgs/$orgId/targets",
            urlParams = urlParams,
        ).map {
            TargetInfo(
                id = it.id,
                type = it.type,
                attributes = defaultJson.decodeFromJsonElement(it.attributes),
                relationships = it.relationships,
            )
        }

    /**
     * [Delete target](https://docs.snyk.io/snyk-api/reference/targets#delete-orgs-org_id-targets-target_id)
     *
     * @param orgId Org ID
     * @param targetId Target ID
     */
    public suspend fun deleteTarget(
        orgId: String,
        targetId: String
    ) {
        client.delete("/rest/orgs/$orgId/targets/$targetId")
    }

    /**
     * [V1: Get Aggregated Project Issues](https://docs.snyk.io/snyk-api/reference/projects-v1#org-orgid-project-projectid-aggregated-issues)
     *
     * @param orgId Org ID
     * @param projectId Project ID
     * @param aggregatedIssuesRequest Additional request config
     */
    public suspend fun getProjectIssuesAggregated(
        orgId: String,
        projectId: String,
        aggregatedIssuesRequest: AggregatedIssuesRequest? = null
    ): Collection<Issue> =
        client
            .post("/v1/org/$orgId/project/$projectId/aggregated-issues") { setBody(aggregatedIssuesRequest) }
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
    public suspend fun getProjectJiraIssues(
        orgId: String,
        projectId: String,
    ): JiraIssuesResponse =
        client
            .get("/v1/org/$orgId/project/$projectId/jira-issues")
            .body()

    /**
     * [V1: Create a new Snyk Org](https://docs.snyk.io/snyk-api/reference/organizations-v1#post-org)
     *
     * @param orgName The name of the new organization.
     * @param groupId The group ID. The API_KEY must have access to this group.
     * @param sourceOrgId ID of an organization to copy settings from. If provided, this organization must be associated with the same group.
     *
     * @return New Org item information.
     */
    public suspend fun createOrg(
        orgName: String,
        groupId: String? = null,
        sourceOrgId: String? = null,
    ): CreateANewOrganizationResponse =
        client
            .post("/v1/org") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateNewOrganizationsBody(
                        name = orgName,
                        groupId = groupId,
                        sourceOrgId = sourceOrgId
                    )
                )
            }.body()

    /**
     * [V1: Ignore a Snyk issue](https://docs.snyk.io/snyk-api/reference/ignores-v1#post-org-orgid-project-projectid-ignore-issueid)
     *
     * @param orgId The organization ID to modify ignores for. The API_KEY must have access to this organization.
     * @param projectId The project ID to modify ignores for.
     * @param issueId The issue ID to modify ignores for. Can be a vulnerability or a license Issue.
     * @param reason The reason that the issue was ignored.
     * @param reasonType The classification of the ignore.
     * @param ignorePath The path to ignore (default is * which represents all paths).
     * @param expires The timestamp that the issue will no longer be ignored.
     * @param disregardIfFixable Only ignore the issue if no upgrade or patch is available.
     *
     * @return New ignore item information.
     */
    public suspend fun ignoreIssue(
        orgId: String,
        projectId: String,
        issueId: String,
        reason: String,
        reasonType: IgnoreReasonType,
        ignorePath: String = "*",
        expires: String? = null,
        disregardIfFixable: Boolean = false,
    ): Collection<Map<String, IgnoreIssueResponse>> =
        client
            .post("/v1/org/$orgId/project/$projectId/ignore/$issueId") {
                contentType(ContentType.Application.Json)
                setBody(
                    IgnoreIssueRequest(
                        reason = reason,
                        reasonType = reasonType,
                        ignorePath = ignorePath,
                        expires = expires,
                        disregardIfFixable = disregardIfFixable,
                    )
                )
            }.body()
}
