package com.jalgoarena.web

import com.jalgoarena.domain.Submission
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestOperations
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CopyOnWriteArrayList

interface SubmissionsClient {
    fun findAll(): List<Submission>
    fun findByProblemId(problemId: String): List<Submission>
    fun findBySubmissionTimeLessThan(tillDate: String): List<Submission>
    fun findAllAfter(id: Int): List<Submission>
}

class HttpSubmissionsClient(
        private val restTemplate: RestOperations,
        private val jalgoarenaApiUrl: String
) : SubmissionsClient {

    override fun findAll() =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions", Array<Submission>::class.java)!!.asList()
            }

    override fun findAllAfter(id: Int) =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/after/$id", Array<Submission>::class.java)!!.asList()
            }

    override fun findByProblemId(problemId: String) =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/problem/$problemId", Array<Submission>::class.java)!!.asList()
            }

    override fun findBySubmissionTimeLessThan(tillDate: String) =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/date/$tillDate", Array<Submission>::class.java)!!.asList()
            }

    private fun <T> handleExceptions(returnOnException: T, body: () -> T) = try {
        body()
    } catch (e: Exception) {
        LOG.error("Error in querying jalgoarena auth service", e)
        returnOnException
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpSubmissionsClient::class.java)
    }
}

class CachedSubmissionsClient(
        private val submissionsClient: SubmissionsClient
) : SubmissionsClient {

    private val submissions = CopyOnWriteArrayList<Submission>()

    init {
        submissions.addAllAbsent(submissionsClient.findAll())
    }

    override fun findAll(): List<Submission> {
        return refreshAndGetSubmissions()
    }

    override fun findAllAfter(id: Int) =
            submissionsClient.findAllAfter(id)

    override fun findByProblemId(problemId: String) =
            refreshAndGetSubmissions().let { submissions ->
                submissions.filter { it.problemId == problemId }
            }

    override fun findBySubmissionTimeLessThan(tillDate: String) =
            takePlusOneDayAtMidnight(tillDate).let { date ->
                refreshAndGetSubmissions().filter {
                    it.submissionTime < date
                }
            }

    private fun takePlusOneDayAtMidnight(tillDate: String) =
            LocalDate.parse(tillDate, YYYY_MM_DD).plusDays(1).atStartOfDay()

    private fun refreshAndGetSubmissions() =
            submissionsClient.findAllAfter(submissionsLastId()).let {
                submissions.addAllAbsent(it)
                submissions
            }

    private fun submissionsLastId() =
            submissions.maxBy { it.id }?.id ?: -1

    companion object {
        private val YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}