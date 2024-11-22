package com.kotlin.azure.function

import com.microsoft.applicationinsights.attach.ApplicationInsights
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val kLogger = KotlinLogging.logger {}

@SpringBootApplication
class EventLoggerApplication

fun main(args: Array<String>) {
	kLogger.info { "Attaching java agent" }
	ApplicationInsights.attach()
	kLogger.info { "Finished attaching java agent" }
	runApplication<EventLoggerApplication>(*args)
}
