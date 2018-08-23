package com.jalgoarena.submissions

import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.SubmissionStats

class SubmissionsFilter {
    companion object {

        fun acceptedWithBestTimes(
                submissions: List<Submission>
        ): List<Submission> {
            val groupByAccepted: Map<String, List<Submission>> = submissions
                    .filter { it.statusCode == "ACCEPTED" }
                    .groupBy { "${it.userId}:${it.problemId}" }

            return groupByAccepted.values
                    .map { it.sortedBy { it.elapsedTime }.first() }
        }

        fun stats(submissions: List<Submission>): SubmissionStats {
            val count = mutableMapOf<String, MutableMap<String, Int>>()

            submissions.forEach { submission ->
                if (!count.contains(submission.userId)) {
                    count[submission.userId] = mutableMapOf()
                }

                if (count[submission.userId]!!.contains(submission.problemId)) {
                    count[submission.userId]!![submission.problemId] =
                            count[submission.userId]!![submission.problemId]!! + 1
                } else {
                    count[submission.userId]!![submission.problemId] = 1
                }
            }

            return SubmissionStats(count)
        }

    }
}