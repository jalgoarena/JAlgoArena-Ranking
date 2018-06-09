package com.jalgoarena.web

import com.jalgoarena.domain.SubmissionStats
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import javax.inject.Inject

@Service
open class HttpSubmissionsClient(
        @Inject private val restTemplate: RestOperations
) : SubmissionsClient {

    override fun stats(): SubmissionStats = restTemplate.getForObject(
            "http://jalgoarena-submissions/submissions/stats", SubmissionStats::class.java
    )
}
