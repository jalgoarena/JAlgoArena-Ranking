package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.domain.Problem
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.client.RestOperations

open class ProblemsClient(
        private val restTemplate: RestOperations,
        private val jalgoarenaApiUrl: String
) : ProblemsRepository {

    @Cacheable("problems")
    override fun findAll(): List<Problem> = restTemplate.getForObject(
            "$jalgoarenaApiUrl/judge/api/problems", Array<Problem>::class.java
    )!!.asList()
}
