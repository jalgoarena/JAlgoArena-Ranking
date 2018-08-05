package com.jalgoarena.domain

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester
import java.time.LocalDateTime
import java.time.Month

class SubmissionSerializationTest {

    private lateinit var json: JacksonTester<Submission>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        objectMapper.findAndRegisterModules()
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_submission() {
        assertThat(json.write(SUBMISSION))
                .isEqualToJson("submission-result.json")
    }

    @Test
    fun should_deserialize_submission() {
        assertThat(json.parse(SUBMISSION_JSON))
                .isEqualTo(SUBMISSION)
    }

    companion object {
        private val SUBMISSION_TIME = LocalDateTime.of(
                2018, Month.JULY, 29, 8, 1, 1
        )

        private val SUBMISSION = Submission(
                id = 2,
                problemId = "fib",
                statusCode = "ACCEPTED",
                userId = "0-0",
                submissionTime = SUBMISSION_TIME,
                elapsedTime = 435.212
        )

        @Language("JSON")
        private val SUBMISSION_JSON = """{
  "id": 2,
  "problemId": "fib",
  "elapsedTime": 435.212,
  "statusCode": "ACCEPTED",
  "userId": "0-0",
  "submissionTime": "2018-07-29T08:01:01"
}
"""
    }
}
