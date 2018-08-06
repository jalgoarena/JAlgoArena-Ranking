package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Submission(
        val id: Int,
        val problemId: String,
        val statusCode: String,
        val userId: String,
        val submissionTime: LocalDateTime,
        val elapsedTime: Double
)
