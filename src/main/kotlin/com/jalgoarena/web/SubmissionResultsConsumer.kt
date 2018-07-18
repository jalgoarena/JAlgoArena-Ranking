package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.RankingEvent
import com.jalgoarena.domain.Submission
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class SubmissionResultsConsumer(
        @Autowired private val submissionsRepository: SubmissionsRepository,
        @Autowired private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var template: KafkaTemplate<Int, RankingEvent>

    @KafkaListener(topics = ["results"])
    fun storeSubmission(message: String) {

        val submission = toSubmission(message)

        logger.info("Received {} [submissionId={}]", "Submission result", submission.submissionId)

        if (submission.statusCode != "ACCEPTED") {
            logger.info("Ignoring [submissionId={}][status={}]",
                    submission.submissionId,
                    submission.statusCode)

            val future = template.send("events", RankingEvent(problemId = submission.problemId))
            future.addCallback(PublishHandler(submission.submissionId))

            return
        }

        if (isValidUser(submission)) {
            submissionsRepository.addOrUpdate(submission)
            logger.info("Submission result is saved [submissionId={}][status={}]",
                    submission.submissionId,
                    submission.statusCode)

            val future = template.send("events", RankingEvent(problemId = submission.problemId))
            future.addCallback(PublishHandler(submission.submissionId))

        } else {
            logger.warn(
                    "Cannot store Submission result [submissionId={}] - authentication failed",
                    submission.submissionId,
                    submission.userId
            )
        }
    }

    private fun toSubmission(message: String): Submission {
        return jacksonObjectMapper().readValue<Submission>(message, Submission::class.java)
    }

    private fun isValidUser(submission: Submission): Boolean {
        val token = submission.token

        if (token == null) {
            logger.warn("Token is empty!")
            return false
        }

        val user = usersClient.findUser(token)

        if (user == null) {
            logger.warn("No user with id: {}", submission.userId)
            return false
        }

        return if (user.id == submission.userId) {
            true
        } else {
            logger.warn("Your are not ADMIN nor the authenticated user is not an owner of submission")
            false
        }
    }

    class PublishHandler(
            private val submissionId: String
    ) : ListenableFutureCallback<SendResult<Int, RankingEvent>> {

        private val logger = LoggerFactory.getLogger(this.javaClass)

        override fun onSuccess(result: SendResult<Int, RankingEvent>?) {
            logger.info("Requested ranking refresh after new submission [submissionId={}]", submissionId)
        }

        override fun onFailure(ex: Throwable?) {
            logger.error("Error during ranking refresh for submission [submissionId={}]", submissionId, ex)
        }

    }
}
