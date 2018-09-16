package com.jalgoarena

import com.jalgoarena.ranking.BasicRankingCalculator
import com.jalgoarena.ranking.BasicScoreCalculator
import com.jalgoarena.web.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@Configuration
open class AppConfiguration {

    @Value("\${jalgoarena.api.url}")
    private lateinit var jalgoarenaApiUrl: String

    @Bean
    open fun rankingCalculator() =
            BasicRankingCalculator(BasicScoreCalculator())

    @Bean
    open fun usersClient(): UsersClient =
            HttpUsersClient(RestTemplate(), jalgoarenaApiUrl)

    @Bean
    open fun problemsClient(): ProblemsClient =
            HttpProblemsClient(RestTemplate(), jalgoarenaApiUrl)

    @Bean
    open fun submissionsClient(): SubmissionsClient =
            CachedSubmissionsClient(
                    HttpSubmissionsClient(RestTemplate(), jalgoarenaApiUrl)
            )
}
