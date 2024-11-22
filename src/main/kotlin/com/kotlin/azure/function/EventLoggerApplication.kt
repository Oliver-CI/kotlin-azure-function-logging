package com.kotlin.azure.function

import com.microsoft.applicationinsights.attach.ApplicationInsights
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventLoggerApplication

fun main(args: Array<String>) {
	ApplicationInsights.attach()
	runApplication<EventLoggerApplication>(*args)
}
