package com.jalgoarena

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
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
            submissionsRepository : SubmissionsRepository,
            problemsClient: ProblemsRepository
    ): RankingCalculator {
        val scoreCalculator = BasicScoreCalculator()
        val rankingCalculator = BasicRankingCalculator(submissionsRepository, scoreCalculator)

        return BonusPointsForBestTimeRankingCalculator(
                submissionsRepository, rankingCalculator
        )
    }

    @Bean
    open fun usersClient(): UsersClient =
            HttpUsersClient(RestTemplate(), jalgoarenaApiUrl)

    @Bean
    open fun problemsClient(): ProblemsRepository =
            ProblemsClient(RestTemplate(), jalgoarenaApiUrl)
}
