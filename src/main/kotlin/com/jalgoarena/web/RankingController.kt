package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.domain.SolvedRatioEntry
import com.jalgoarena.domain.Submission
import com.jalgoarena.ranking.RankingCalculator
import com.jalgoarena.ranking.RankingCalculator.Companion.acceptedWithBestTimes
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.transaction.TransactionException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
class RankingController(
        @Autowired private val rankingCalculator: RankingCalculator,
        @Autowired private val usersClient: UsersClient,
        @Autowired private val problemsRepository: ProblemsRepository,
        @Autowired private val submissionsRepository: SubmissionsRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/ranking", produces = ["application/json"])
    fun ranking(): List<RankEntry> = try {
        rankingCalculator.ranking(
                users = usersClient.findAllUsers().filter {
                    it.username.toLowerCase() != "admin"
                },
                submissions = acceptedWithBestTimes(submissionsRepository.findAll()),
                problems = problemsRepository.findAll()
        )
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf()
    }

    @GetMapping("/ranking/{date}", produces = ["application/json"])
    fun rankingTillDate(@PathVariable date: String): List<RankEntry> = try {
        val tillDate = LocalDate.parse(date, YYYY_MM_DD).plusDays(1).atStartOfDay()

        rankingCalculator.ranking(
                users = usersClient.findAllUsers().filter {
                    it.username.toLowerCase() != "admin"
                },
                submissions = acceptedWithBestTimes(submissionsRepository.findBySubmissionTimeLessThan(tillDate)),
                problems = problemsRepository.findAll()
        )
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf()
    }

    @GetMapping("/ranking/problem/{problemId}", produces = ["application/json"])
    fun problemRanking(@PathVariable problemId: String) = try {
            rankingCalculator.problemRanking(
                    problemId = problemId,
                    users = usersClient.findAllUsers().filter {
                        it.username.toLowerCase() != "admin"
                    },
                    problems = problemsRepository.findAll())
    } catch (e: DataAccessException) {
        logger.error("Cannot connect to database", e)
        listOf<ProblemRankEntry>()
    }

    @GetMapping("/solved-ratio", produces = ["application/json"])
    fun submissionsSolvedRatio() = try {
            calculateSubmissionsSolvedRatioAndReturnIt(submissionsRepository.findAll())
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf<SolvedRatioEntry>()
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
