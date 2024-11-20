package com.kotlin.azure.event_logger.applicationinsights

import com.kotlin.azure.event_logger.logger.Logger
import com.kotlin.azure.event_logger.logger.Metrics
import com.kotlin.azure.event_logger.logger.Properties
import com.microsoft.applicationinsights.TelemetryClient
import com.microsoft.applicationinsights.telemetry.EventTelemetry
import com.microsoft.applicationinsights.telemetry.MetricTelemetry
import com.microsoft.applicationinsights.telemetry.SeverityLevel
import com.microsoft.applicationinsights.telemetry.TraceTelemetry
import org.slf4j.MDC
import kotlin.reflect.KClass

private const val PROPERTY_THREAD = "Thread"
private const val PROPERTY_CLASS = "Class"

class ApplicationInsightsLogger(
    private val telemetryClient: TelemetryClient,
    private val kClass: KClass<*>
) : Logger {

    override fun verbose(message: () -> Any) {
        trace(message, SeverityLevel.Verbose)
    }

    override fun verbose(properties: Properties, message: () -> Any) {
        trace(message, SeverityLevel.Verbose, properties)
    }

    override fun info(message: () -> Any) {
        trace(message, SeverityLevel.Information)
    }

    override fun info(properties: Properties, message: () -> Any) {
        trace(message, SeverityLevel.Information, properties)
    }

    override fun warn(message: () -> Any) {
        trace(message, SeverityLevel.Warning)
    }

    override fun warn(properties: Properties, message: () -> Any) {
        trace(message, SeverityLevel.Warning, properties)
    }

    override fun error(message: () -> Any) {
        trace(message, SeverityLevel.Error)
    }

    override fun error(properties: Properties, message: () -> Any) {
        trace(message, SeverityLevel.Error, properties)
    }

    override fun critical(message: () -> Any) {
        trace(message, SeverityLevel.Critical)
    }

    override fun critical(properties: Properties, message: () -> Any) {
        trace(message, SeverityLevel.Critical, properties)
    }

    override fun event(name: String, properties: Properties?, metrics: Metrics?) {
        telemetryClient.trackEvent(
            EventTelemetry(name).apply {
                getAllNonNullableProperties(properties).forEach { this.properties[it.key] = it.value }
                metrics?.filterValues { it != null }?.map { this.metrics[it.key] = it.value }
            }
        )
    }

    override fun metric(name: String, value: Double) {
        telemetryClient.trackMetric(MetricTelemetry(name, value))
    }

    private fun trace(message: () -> Any, severityLevel: SeverityLevel, properties: Properties? = null) {
        telemetryClient.trackTrace(
            TraceTelemetry(message.invoke().toString(), severityLevel).apply {
                getAllNonNullableProperties(properties).forEach { this.properties[it.key] = it.value }
            }
        )
    }

    private fun getAllNonNullableProperties(properties: Properties?) = mutableMapOf<String, String>().apply {
        properties?.filterValues { it != null }?.forEach { this[it.key] = it.value.toString() }
        MDC.getCopyOfContextMap()?.filterValues { it != null }?.forEach {
            this[it.key] = it.value.toString()
        }
        this[PROPERTY_THREAD] = Thread.currentThread().name
        this[PROPERTY_CLASS] = kClass.java.name
    }

}