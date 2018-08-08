package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableCaching(proxyTargetClass=true)
@EnableScheduling
open class JAlgoArenaRankingApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaRankingApp::class.java, *args)
}