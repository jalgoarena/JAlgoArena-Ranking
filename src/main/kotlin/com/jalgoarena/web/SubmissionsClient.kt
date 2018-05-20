package com.jalgoarena.web

import com.jalgoarena.domain.SubmissionStats

interface SubmissionsClient {
    fun stats(): SubmissionStats
}
