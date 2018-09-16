package com.jalgoarena.web

import com.jalgoarena.domain.Submission
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestOperations
import java.util.*
import java.util.concurrent.atomic.AtomicReference

interface SubmissionsClient {
    fun findAll(): List<Submission>
    fun findByProblemId(problemId: String): List<Submission>
    fun findBySubmissionTimeLessThan(tillDate: String): List<Submission>
    fun findAllAfter(id: Int): List<Submission>
    fun count(): Int
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

    override fun count() =
            handleExceptions(returnOnException = 0) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/count", Int::class.java)!!
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

    private var submissionsCache: AtomicReference<Optional<List<Submission>>> =
            AtomicReference(Optional.empty())

    override fun findAll(): List<Submission> {
        val currentSubmissionsCount = submissionsClient.count()
        val optionalSubmissions = submissionsCache.get()

        if (optionalSubmissions.isPresent && optionalSubmissions.get().size == currentSubmissionsCount) {
            return optionalSubmissions.get()
        }

        val submissions = submissionsClient.findAll()
        this.submissionsCache.set(Optional.of(submissions))

        return submissions
    }

    override fun findByProblemId(problemId: String) = submissionsClient.findByProblemId(problemId)

    override fun findBySubmissionTimeLessThan(tillDate: String) =
            submissionsClient.findBySubmissionTimeLessThan(tillDate)

    override fun findAllAfter(id: Int) = submissionsClient.findAllAfter(id)

    override fun count() = submissionsClient.count()
}