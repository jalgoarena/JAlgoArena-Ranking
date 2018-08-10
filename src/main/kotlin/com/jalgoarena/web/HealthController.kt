package com.jalgoarena.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(
        @Autowired private val usersClient: UsersClient,
        @Autowired private val submissionsClient: SubmissionsClient
) {

    @GetMapping("/ranking/health")
    fun rankingHealth(): HealthStats {
        val users = usersClient.findAllUsers()
        val submissions = submissionsClient.findAll()

        return HealthStats(
                users.count(),
                submissions.count(),
                submissions.maxBy { it.id }?.id ?: -1
        )
    }

    data class HealthStats(
            val usersCount: Int,
            val submissionsCount: Int,
            val submissionsMaxId: Int
    )
}