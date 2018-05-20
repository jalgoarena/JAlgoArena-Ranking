package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.Submission

class BasicScoreCalculator : ScoreCalculator {

    override fun calculate(userSubmission: Submission, problem: Problem, problemSubmissionsCount: Int): Double {
        val (_, level, timeLimit) = problem

        val basePointsPerLevel = (level - 1) * 20
        val basePoints = basePointsPerLevel + timeFactor(
                userSubmission.elapsedTime / timeLimit
        )

        return Math.max(basePoints - (problemSubmissionsCount - 1), 1.0)
    }

    private fun timeFactor(elapsedTime: Double) =
            when {
                elapsedTime >= 500 -> 1.0
                elapsedTime >= 100 -> 3.0
                elapsedTime >= 10 -> 5.0
                elapsedTime >= 1 -> 8.0
                else -> 10.0
            }
}
