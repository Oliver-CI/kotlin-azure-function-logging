package com.kotlin.azure.function.applicationinsights

import com.kotlin.azure.function.logger.LoggerFactory
import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ApplicationInsightsLoggerConfig {

    @Bean("loggerFactory")
    @Profile(value = ["!local"])
    fun applicationInsightsLoggerFactory() = ApplicationInsightsLoggerFactory(
        TelemetryClient()
    ).apply {
        println("Application Insights logger active")
        LoggerFactory.setLoggerFactory(this)
    }

}