package com.jalgoarena.web

import com.jalgoarena.domain.SubmissionStats
import org.springframework.web.client.RestOperations

class HttpSubmissionsClient(
        private val restTemplate: RestOperations,
        private val jalgoarenaApiUrl: String
) : SubmissionsClient {

    override fun stats(): SubmissionStats = restTemplate.getForObject(
            "$jalgoarenaApiUrl/submissions/api/submissions/stats", SubmissionStats::class.java
    )!!
}
