package com.kotlin.azure.function

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporter
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class EventLoggerApplication

fun main(args: Array<String>) {
	val sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder()
	AzureMonitorExporter.customize(sdkBuilder)
	val openTelemetry: OpenTelemetry = sdkBuilder.build().openTelemetrySdk
	OpenTelemetryAppender.install(openTelemetry)
	runApplication<EventLoggerApplication>(*args)
}
