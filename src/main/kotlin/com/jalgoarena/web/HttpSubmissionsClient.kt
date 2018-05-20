package com.jalgoarena.web

import com.jalgoarena.domain.SubmissionStats
import com.netflix.discovery.EurekaClient
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import javax.inject.Inject

@Service
open class HttpSubmissionsClient(
        @Inject private val discoveryClient: EurekaClient,
        @Inject private val restTemplate: RestOperations
) : SubmissionsClient {

    private fun submissionsServiceUrl(): String =
            discoveryClient.getNextServerFromEureka("jalgoarena-submissions", false).homePageUrl

    override fun stats(): SubmissionStats = restTemplate.getForObject(
            "${submissionsServiceUrl()}/submissions/stats", SubmissionStats::class.java
    )
}
