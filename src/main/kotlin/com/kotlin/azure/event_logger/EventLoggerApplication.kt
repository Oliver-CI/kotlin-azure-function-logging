package com.kotlin.azure.event_logger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventLoggerApplication

fun main(args: Array<String>) {
	runApplication<EventLoggerApplication>(*args)
}
