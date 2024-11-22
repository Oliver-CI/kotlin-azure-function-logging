package com.kotlin.azure.function.applicationinsights

import com.microsoft.applicationinsights.TelemetryClient
import com.kotlin.azure.function.logger.LoggerFactory
import kotlin.reflect.KClass

class ApplicationInsightsLoggerFactory(private val telemetryClient: TelemetryClient) : LoggerFactory {
    override fun apply(kClass: KClass<*>) = ApplicationInsightsLogger(telemetryClient, kClass)
}