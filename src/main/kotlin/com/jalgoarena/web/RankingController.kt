package com.jalgoarena.web

import com.jalgoarena.domain.SolvedRatioEntry
import com.jalgoarena.domain.Submission
import com.jalgoarena.ranking.RankingCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
open class RankingController(
        @Autowired private val rankingCalculator: RankingCalculator,
        @Autowired private val usersClient: UsersClient,
        @Autowired private val problemsClient: ProblemsClient,
        @Autowired private val submissionsClient: SubmissionsClient
) {

    @GetMapping("/ranking", produces = ["application/json"])
    open fun ranking() =
            rankingCalculator.ranking(
                    users = usersClient.findAllUsers(),
                    allSubmissions = submissionsClient.findAll(),
                    problems = problemsClient.findAll()
            )

    @GetMapping("/ranking/{date}", produces = ["application/json"])
    open fun rankingTillDate(@PathVariable date: String) =
            rankingCalculator.ranking(
                    users = usersClient.findAllUsers(),
                    allSubmissions = submissionsClient.findBySubmissionTimeLessThan(date),
                    problems = problemsClient.findAll()
            )

    @GetMapping("/ranking/startDate", produces = ["application/json"])
    open fun rankingStartDate(): String {
        val submission = submissionsClient.findAll().minBy { it.submissionTime }

        return if (submission == null) {
            yesterday()
        } else {
            aDayBefore(submission.submissionTime)
        }
    }

    @GetMapping("/ranking/problem/{problemId}", produces = ["application/json"])
    open fun problemRanking(@PathVariable problemId: String) =
            rankingCalculator.problemRanking(
                    users = usersClient.findAllUsers(),
                    problemSubmissions = submissionsClient.findByProblemId(problemId),
                    problems = problemsClient.findAll(),
                    problemId = problemId
            )

    @GetMapping("/solved-ratio", produces = ["application/json"])
    open fun submissionsSolvedRatio() =
            calculateSubmissionsSolvedRatioAndReturnIt(submissionsClient.findAll().filter { it.statusCode == "ACCEPTED" })

    private fun aDayBefore(submissionTime: LocalDateTime) =
            submissionTime.minusDays(1).format(YYYY_MM_DD)

    private fun yesterday() =
            LocalDateTime.now().minusDays(1).format(YYYY_MM_DD)

    private fun calculateSubmissionsSolvedRatioAndReturnIt(submissions: List<Submission>) =
            submissions
                    .distinctBy { Pair(it.userId, it.problemId) }
                    .groupBy { it.problemId }
                    .map {
                        SolvedRatioEntry(it.key, it.value.count())
                    }

    companion object {
        private val YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
