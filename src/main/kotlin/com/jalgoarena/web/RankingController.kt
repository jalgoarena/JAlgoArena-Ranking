package com.jalgoarena.web

import com.jalgoarena.domain.*
import com.jalgoarena.ranking.RankingCalculator
import com.jalgoarena.ranking.RankingCalculator.Companion.acceptedWithBestTimes
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
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

    @Cacheable("ranking")
    @GetMapping("/ranking", produces = ["application/json"])
    open fun ranking() = rankingCalculator.ranking(
            users = allUsersWithoutAdmin(),
            submissions = acceptedWithBestTimes(submissionsClient.findAll()),
            problems = problemsClient.findAll()
    )

    @Cacheable("rankingTillDate", key = "#date")
    @GetMapping("/ranking/{date}", produces = ["application/json"])
    open fun rankingTillDate(@PathVariable date: String) = rankingCalculator.ranking(
            users = allUsersWithoutAdmin(),
            submissions = acceptedWithBestTimes(submissionsClient.findBySubmissionTimeLessThan(date)),
            problems = problemsClient.findAll()
    )

    @Cacheable("startDate")
    @GetMapping("/ranking/startDate", produces = ["application/json"])
    open fun rankingStartDate(): String {
        val submission = submissionsClient.findAll().minBy { it.submissionTime }

        return if (submission == null) {
            yesterday()
        } else {
            aDayBefore(submission.submissionTime)
        }
    }

    @Cacheable("problemRanking", key = "#problemId")
    @GetMapping("/ranking/problem/{problemId}", produces = ["application/json"])
    open fun problemRanking(@PathVariable problemId: String) = rankingCalculator.problemRanking(
            problemId = problemId,
            users = allUsersWithoutAdmin(),
            problems = problemsClient.findAll()
    )

    @Cacheable("solvedRatio")
    @GetMapping("/solved-ratio", produces = ["application/json"])
    open fun submissionsSolvedRatio() =
        calculateSubmissionsSolvedRatioAndReturnIt(submissionsClient.findAll())

    private fun aDayBefore(submissionTime: LocalDateTime) =
            submissionTime.minusDays(1).format(YYYY_MM_DD)

    private fun yesterday() =
            LocalDateTime.now().minusDays(1).format(YYYY_MM_DD)

    private fun allUsersWithoutAdmin(): List<User> = usersClient.findAllUsers().filter {
        it.username.toLowerCase() != "admin"
    }

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
