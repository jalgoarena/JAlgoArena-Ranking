package com.jalgoarena.ranking

import com.jalgoarena.domain.*

class BonusPointsForBestTimeRankingCalculator(
        private val rankingCalculator: RankingCalculator
) : RankingCalculator {

    override fun ranking(
            users: List<User>, allSubmissions: List<Submission>, problems: List<Problem>
    ): List<RankEntry> {

        val bonusPoints = calculateBonusPointsForFastestSolutions(allSubmissions, users)

        return rankingCalculator.ranking(users, allSubmissions, problems).map { rankEntry ->
            val id = users.firstOrNull { it.username == rankEntry.hacker }?.id ?: ""

            RankEntry(
                    rankEntry.hacker,
                    rankEntry.score + bonusPoints[id] as Double,
                    rankEntry.solvedProblems,
                    rankEntry.region,
                    rankEntry.team
            )
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(
            users: List<User>, problemSubmissions: List<Submission>, problems: List<Problem>, problemId: String
    ): List<ProblemRankEntry> {

        val bonusPoints = calculateBonusPointsForFastestSolutions(problemSubmissions, users)

        return rankingCalculator
                .problemRanking(users, problemSubmissions, problems, problemId).map { problemRankEntry ->
                    val user = users.first { it.username == problemRankEntry.hacker }

                    ProblemRankEntry(
                            problemRankEntry.hacker,
                            problemRankEntry.score + bonusPoints[user.id] as Double,
                            problemRankEntry.elapsedTime
                    )
                }.sortedBy { it.elapsedTime }
    }

    private fun calculateBonusPointsForFastestSolutions(submissions: List<Submission>, users: List<User>): Map<String, Double> {

        val bonusPoints = mutableMapOf<String, Double>()
        users.forEach { bonusPoints[it.id] = 0.0 }

        val problems = submissions.map { it.problemId }.distinct()

        problems.forEach { problem ->
            val problemSubmissions = submissions.filter { it.problemId == problem }
            val fastestSubmission = problemSubmissions.minBy { it.elapsedTime }

            if (fastestSubmission != null && bonusPoints[fastestSubmission.userId] != null) {
                bonusPoints[fastestSubmission.userId] = bonusPoints[fastestSubmission.userId] as Double + 1.0
            }
        }

        return bonusPoints
    }
}
