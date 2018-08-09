package com.jalgoarena.ranking

import com.jalgoarena.domain.*
import com.jalgoarena.submissions.SubmissionsFilter

class BasicRankingCalculator(
        private val scoreCalculator: ScoreCalculator
) : RankingCalculator {

    override fun ranking(
            users: List<User>, allSubmissions: List<Submission>, problems: List<Problem>
    ): List<RankEntry> {

        val stats = SubmissionsFilter.stats(allSubmissions)
        val submissions = SubmissionsFilter.acceptedWithBestTimes(allSubmissions)

        return users.map { user ->

            val userSubmissionsCount = stats.count[user.id].orEmpty()

            val userSubmissions = submissions
                    .filter { it.userId == user.id }
                    .sortedBy { it.elapsedTime }
                    .distinctBy { it.problemId }
            val solvedProblems = userSubmissions.map { it.problemId }

            RankEntry(
                    user.username,
                    score(userSubmissions, problems, userSubmissionsCount),
                    solvedProblems,
                    user.region,
                    user.team
            )
        }.sortedWith(compareByDescending(RankEntry::score).thenByDescending { it.solvedProblems.size })
    }

    override fun problemRanking(
            users: List<User>, problemSubmissions: List<Submission>, problems: List<Problem>, problemId: String
    ): List<ProblemRankEntry> {

        val stats = SubmissionsFilter.stats(problemSubmissions)

        return problemSubmissions.map { submission ->
            val user = users.first { it.id == submission.userId }

            val userSubmissionsCount = stats.count[user.id]!!

            ProblemRankEntry(
                    user.username,
                    score(listOf(submission), problems, userSubmissionsCount),
                    submission.elapsedTime
            )
        }.sortedBy { it.elapsedTime }.distinctBy { it.hacker }
    }

    private fun score(userSubmissions: List<Submission>, problems: List<Problem>, userSubmissionsCount: Map<String, Int>): Double {
        return userSubmissions.sumByDouble { userSubmission ->
            val problem = problems.first { it.id == userSubmission.problemId }
            val problemSubmissionsCount = userSubmissionsCount[problem.id]
            scoreCalculator.calculate(
                    userSubmission, problem, problemSubmissionsCount ?: 1
            )
        }
    }
}
