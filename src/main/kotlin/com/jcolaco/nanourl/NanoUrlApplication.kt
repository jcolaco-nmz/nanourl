package com.jcolaco.nanourl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NanoUrlApplication

fun main(args: Array<String>) {
	runApplication<NanoUrlApplication>(*args)
}
