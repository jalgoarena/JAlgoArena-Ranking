package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching(proxyTargetClass=true)
open class JAlgoArenaRankingApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaRankingApp::class.java, *args)
}