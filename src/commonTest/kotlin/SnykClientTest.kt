import dev.samoylenko.client.snyk.SnykClient
import dev.samoylenko.client.snyk.model.request.AggregatedIssuesRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class SnykClientTest {

    companion object {
        private val logger = KotlinLogging.logger {}

        private const val MAX_TAKE = 5

        private val snykClient = SnykClient()

        // Snyk Jira API currently doesn't support IaC projects: https://docs.snyk.io/scan-with-snyk/snyk-iac/snyk-iac-integrations/jira-integration-for-iac
        // Enum: https://docs.snyk.io/snyk-api/api-endpoints-index-and-tips/project-type-responses-from-the-api
        private val unsupportedTypesJiraApi = """
            armconfig
            cloudformationconfig
            helmconfig
            k8sconfig
            terraformconfig
            terraformplan
        """.trimIndent().lines()
    }

    @Test
    fun smokeTest() = runTest(timeout = 5.minutes) {

        // Test getting orgs
        val snykOrgs = snykClient.getOrgs()
        assertTrue("Must be at least one Snyk org to test with") { snykOrgs.isNotEmpty() }

        snykOrgs.forEach {
            logger.info { "${it.id} - ${it.attributes.slug} :: ${it.attributes.name}" }
            // assertDoesNotThrow
            assertNotEquals(Uuid.NIL, Uuid.parse(it.id))
        }

        // Test getting projects
        val orgProjects = snykOrgs.take(MAX_TAKE).associateWith {
            snykClient.getOrgProjects(it.id)
                // Remove unsupported projects of unsupported types
                .filterNot { projectInfo -> projectInfo.type.lowercase() in unsupportedTypesJiraApi }
        }
        assertTrue("Must be at least one Snyk project to test with") { orgProjects.isNotEmpty() }

        orgProjects.forEach { (orgInfo, projects) ->
            projects
                .forEach { project ->
                    logger.info { "${orgInfo.id} :: ${project.id} - ${project.attributes.name}" }
                    // assertDoesNotThrow
                    assertNotEquals(Uuid.NIL, Uuid.parse(project.id))
                }
        }

        var issueCount = 0
        var jiraIssueCount = 0

        orgProjects.asIterable().take(MAX_TAKE).forEach { (orgInfo, projects) ->
            projects.asIterable().take(MAX_TAKE).forEach { project ->

                // Rate limiting
                delay(50.milliseconds)

                // Test getting an aggregated issue report
                val projectIssuesAggregated = snykClient.getProjectIssuesAggregated(
                    orgInfo.id,
                    project.id,
                    AggregatedIssuesRequest(
                        includeDescription = true,
                        includeIntroducedThrough = true
                    )
                )

                val projectJiraIssues = snykClient.getProjectJiraIssues(orgInfo.id, project.id)

                logger.info { "${project.attributes.name} :: ${projectIssuesAggregated.size} issues ::  ${projectJiraIssues.size} in JIRA" }

                issueCount += projectIssuesAggregated.size
                jiraIssueCount += projectJiraIssues.size
            }
        }

        assertTrue("Must be at least one issue to test with") { issueCount > 0 }
    }
}
