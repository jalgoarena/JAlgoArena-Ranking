package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.domain.Problem
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.client.RestOperations

open class ProblemsClient(
        private val restTemplate: RestOperations,
        private val jalgoarenaApiUrl: String
) : ProblemsRepository {

    @Cacheable("problems")
    override fun findAll(): List<Problem> = handleExceptions(returnOnException = emptyList()) {
        restTemplate.getForObject(
                "$jalgoarenaApiUrl/judge/api/problems", Array<Problem>::class.java
        )!!.asList()
    }

    private fun <T> handleExceptions(returnOnException: T, body: () -> T) = try {
        body()
    } catch (e: Exception) {
        LOG.error("[err] GET $jalgoarenaApiUrl/judge/api/problems: ", e)
        returnOnException
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpUsersClient::class.java)
    }
}

