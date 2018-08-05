package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GenericEvent(
        val type: String,
        val problemId: String,
        val submissionId: String
) {
    companion object {
        const val REFRESH_SUBMISSIONS_EVENT = "refreshUserSubmissions"
        const val REFRESH_RANKING_EVENT = "refreshRanking"
    }
}