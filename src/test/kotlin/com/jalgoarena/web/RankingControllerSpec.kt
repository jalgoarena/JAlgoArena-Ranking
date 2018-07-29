package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.domain.Submission
import com.jalgoarena.ranking.RankingCalculator
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@WebFluxTest(RankingController::class)
class RankingControllerSpec {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var usersClient: UsersClient

    @MockBean
    private lateinit var rankingCalculator: RankingCalculator

    @MockBean
    private lateinit var problemsRepository: ProblemsRepository

    @MockBean
    private lateinit var submissionsRepository: SubmissionsRepository

    @Test
    fun returns_200_and_submissions_solved_ratio_list() {
        given(submissionsRepository.findAll()).willReturn(listOf(
                submissionForProblem("fib", "user1", 1),
                submissionForProblem("fib", "user2", 2),
                submissionForProblem("fib", "user3", 3),
                submissionForProblem("2-sum", "user1", 4),
                submissionForProblem("2-sum", "user2", 5)
        ))

        webTestClient.get()
                .uri("/solved-ratio")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$").isArray
                .jsonPath("$[0].problemId").isEqualTo("fib")
                .jsonPath("$[0].solutionsCount").isEqualTo(3)
    }

    @Test
    fun returns_200_and_ranking() {
        given(usersClient.findAllUsers()).willReturn(emptyList())
        given(submissionsRepository.findAll()).willReturn(emptyList())
        given(problemsRepository.findAll()).willReturn(emptyList())

        given(rankingCalculator.ranking(emptyList(), emptyList(), emptyList())).willReturn(listOf(
                RankEntry("mikołaj", 40.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("julia", 40.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("joe", 20.0, listOf("2-sum"), "London", "London Team"),
                RankEntry("tom", 20.0, listOf("2-sum"), "London", "London Team")
        ))

        webTestClient.get()
                .uri("/ranking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4)
    }

    @Test
    fun returns_200_and_problem_ranking() {
        given(usersClient.findAllUsers()).willReturn(emptyList())
        given(problemsRepository.findAll()).willReturn(emptyList())

        given(rankingCalculator.problemRanking("fib", emptyList(), emptyList())).willReturn(listOf(
                ProblemRankEntry("julia", 10.0, 0.0001),
                ProblemRankEntry("joe", 10.0, 0.001),
                ProblemRankEntry("mikołaj", 10.0, 0.01)
        ))

        webTestClient.get()
                .uri("/ranking/problem/fib")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
    }

    private fun submissionForProblem(problemId: String, userId: String, id: Int) =
            Submission(
                    problemId,
                    "class Solution",
                    "ACCEPTED",
                    userId,
                    "2",
                    LocalDateTime.now(),
                    0.5,
                    10L,
                    null,
                    1,
                    0,
                    "dummy_token",
                    id
            )
}
