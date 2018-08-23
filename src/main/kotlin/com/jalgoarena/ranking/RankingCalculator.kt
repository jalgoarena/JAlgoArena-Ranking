package com.jalgoarena.ranking

import com.jalgoarena.domain.*

interface RankingCalculator {
    fun ranking(
            users: List<User>, allSubmissions: List<Submission>, problems: List<Problem>
    ): List<RankEntry>

    fun problemRanking(
            users: List<User>, problemSubmissions: List<Submission>, problems: List<Problem>, problemId: String
    ): List<ProblemRankEntry>
}
