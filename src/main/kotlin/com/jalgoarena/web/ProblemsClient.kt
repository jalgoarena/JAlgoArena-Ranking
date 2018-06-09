package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.domain.Problem
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import javax.inject.Inject

@Service
open class ProblemsClient(
        @Inject private val restTemplate: RestOperations
) : ProblemsRepository {

    @Cacheable("problems")
    override fun findAll(): List<Problem> = restTemplate.getForObject(
            "http://jalgoarena-judge/problems", Array<Problem>::class.java
    ).asList()
}
