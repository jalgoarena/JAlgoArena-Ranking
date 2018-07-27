package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.*
import com.jalgoarena.ranking.RankingCalculator.Companion.acceptedWithBestTimes

class BasicRankingCalculator(
        private val submissionsRepository: SubmissionsRepository,
        private val scoreCalculator: ScoreCalculator
) : RankingCalculator {

    override fun ranking(
            users: List<User>, submissions: List<Submission>, problems: List<Problem>
    ): List<RankEntry> {

        val stats = stats(submissionsRepository.findAll())

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
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(problemId: String, users: List<User>, problems: List<Problem>): List<ProblemRankEntry> {

        val stats = stats(submissionsRepository.findAll())
        val problemSubmissions = acceptedWithBestTimes(submissionsRepository.findByProblemId(problemId))

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

    private fun stats(submissions: List<Submission>): SubmissionStats {
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

