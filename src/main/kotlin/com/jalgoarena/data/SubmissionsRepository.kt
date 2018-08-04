package com.jalgoarena.data

import com.jalgoarena.domain.Submission
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface SubmissionsRepository : JpaRepository<Submission, Int> {
    fun findByProblemId(problemId: String): List<Submission>
    fun findBySubmissionTimeLessThan(tillDate: LocalDateTime): List<Submission>
}

