package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.*
import com.jalgoarena.web.SubmissionsClient

class BasicRankingCalculator(
        private val submissionsClient: SubmissionsClient,
        submissionsRepository: SubmissionsRepository,
        scoreCalculator: ScoreCalculator
) : RankingCalculator,
        ScoreCalculator by scoreCalculator,
        SubmissionsRepository by submissionsRepository {

    override fun ranking(users: List<User>, submissions: List<Submission>, problems: List<Problem>): List<RankEntry> {

        val stats = submissionsClient.stats()

        return users.map { user ->

            val userSubmissionsCount = stats.count[user.id].orEmpty()

            val userSubmissions = submissions
                    .filter { it.userId == user.id && it.statusCode == "ACCEPTED"}
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
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(problemId: String, users: List<User>, problems: List<Problem>): List<ProblemRankEntry> {

        val stats = submissionsClient.stats()
        val problemSubmissions = findByProblemId(problemId)

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
            calculate(userSubmission, problem, problemSubmissionsCount ?: 1)
        }
    }
}

