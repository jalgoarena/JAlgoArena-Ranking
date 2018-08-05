package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "submissions")
data class Submission(
        @Id
        var id: Int = -1,
        @Column(nullable = false)
        var problemId: String = "",
        @Column(nullable = false)
        var statusCode: String = "NOT_FOUND",
        @Column(nullable = false)
        var userId: String = "",
        @Column(nullable = false)
        var submissionTime: LocalDateTime = LocalDateTime.now(),
        @Column(nullable = false)
        var elapsedTime: Double = -1.0
)
