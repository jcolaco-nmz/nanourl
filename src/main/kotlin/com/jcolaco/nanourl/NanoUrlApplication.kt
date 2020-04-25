package com.jcolaco.nanourl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class NanoUrlApplication

fun main(args: Array<String>) {
    runApplication<NanoUrlApplication>(*args)
}
