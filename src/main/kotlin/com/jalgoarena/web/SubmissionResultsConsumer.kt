package com.jalgoarena.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.domain.GenericEvent
import com.jalgoarena.domain.GenericEvent.Companion.REFRESH_RANKING_EVENT
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class SubmissionResultsConsumer(
        @Autowired private val cacheManager: CacheManager,
        @Autowired private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var rankingEventPublisher: KafkaTemplate<Int, String>

    @KafkaListener(topics = ["events"])
    fun refreshRankings(message: String) {

        try {
            val event = toGenericEvent(message)

            if (event.type != GenericEvent.REFRESH_SUBMISSIONS_EVENT) {
                return
            }

            logger.info("Received request for refreshing rankings: $event")
            clearCache()

            val rankingEventFuture = rankingEventPublisher.send(
                "events", objectMapper.writeValueAsString(GenericEvent(REFRESH_RANKING_EVENT))
            )
            rankingEventFuture.addCallback(RankingEventPublishHandler())
        } catch (ex: Exception) {
            logger.error("Cannot refresh ranking: {}", message, ex)
            throw ex
        }
    }

    @Scheduled(fixedRate = 30000, initialDelay = 30000)
    fun clearCache() {
        cacheManager.cacheNames.parallelStream().forEach {
            cacheManager.getCache(it)!!.clear()
        }
    }

    private fun toGenericEvent(message: String): GenericEvent {
        return objectMapper.readValue<GenericEvent>(message, GenericEvent::class.java)
    }

    class RankingEventPublishHandler : ListenableFutureCallback<SendResult<Int, String>> {

        private val logger = LoggerFactory.getLogger(this.javaClass)

        override fun onSuccess(result: SendResult<Int, String>?) {
            logger.info("Requested ranking refresh")
        }

        override fun onFailure(ex: Throwable?) {
            logger.error("Error during ranking refresh", ex)
        }
    }
}
