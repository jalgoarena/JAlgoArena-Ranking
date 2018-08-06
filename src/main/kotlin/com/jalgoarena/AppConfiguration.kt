package com.jalgoarena

import com.jalgoarena.web.ProblemsClient
import com.jalgoarena.ranking.BasicRankingCalculator
import com.jalgoarena.ranking.BasicScoreCalculator
import com.jalgoarena.ranking.BonusPointsForBestTimeRankingCalculator
import com.jalgoarena.ranking.RankingCalculator
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
    open fun rankingCalculator(
            submissionsClient : SubmissionsClient,
            problemsClient: ProblemsClient
    ): RankingCalculator {
        val scoreCalculator = BasicScoreCalculator()
        val rankingCalculator = BasicRankingCalculator(submissionsClient, scoreCalculator)

        return BonusPointsForBestTimeRankingCalculator(
                submissionsClient, rankingCalculator
        )
    }

    @Bean
    open fun usersClient(): UsersClient =
            HttpUsersClient(RestTemplate(), jalgoarenaApiUrl)

    @Bean
    open fun problemsClient(): ProblemsClient =
            HttpProblemsClient(RestTemplate(), jalgoarenaApiUrl)

    @Bean
    open fun submissionsClient(): SubmissionsClient =
            HttpSubmissionsClient(RestTemplate(), jalgoarenaApiUrl)
}
