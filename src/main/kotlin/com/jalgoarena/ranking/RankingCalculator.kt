package com.jalgoarena.ranking

import com.jalgoarena.domain.*

interface RankingCalculator {
    fun ranking(
            users: List<User>,
            submissions: List<Submission>,
            problems: List<Problem>
    ): List<RankEntry>

    fun problemRanking(
            problemId: String,
            users: List<User>,
            problems: List<Problem>
    ): List<ProblemRankEntry>


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
    }
}
