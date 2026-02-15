package dev.samoylenko.client.snyk

import dev.samoylenko.client.snyk.model.response.ProjectInfo

public object Extensions {
    // Enum: https://docs.snyk.io/snyk-api/api-endpoints-index-and-tips/project-type-responses-from-the-api
    private val unsupportedTypesJiraApi = """
            armconfig
            cloudformationconfig
            helmconfig
            k8sconfig
            terraformconfig
            terraformplan
        """.trimIndent().lines()

    /**
     * Checks if the project type is supported by the Snyk Jira API.
     * Snyk Jira API currently doesn't support IaC projects: https://docs.snyk.io/scan-with-snyk/snyk-iac/snyk-iac-integrations/jira-integration-for-iac
     */
    public fun ProjectInfo.isSupportedByJiraApi(): Boolean = type !in unsupportedTypesJiraApi
}
