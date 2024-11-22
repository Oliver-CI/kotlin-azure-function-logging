package com.kotlin.azure.function.functions

import com.kotlin.azure.function.logger.Logger
import com.kotlin.azure.function.logger.LoggerFactory
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.*
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.stereotype.Component

val kLogger = KotlinLogging.logger {}

@Component
class LogFunction {

    @FunctionName("CustomAppLogger")
    fun handleCustomAppLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "custom-app-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        withLoggingContext(
            mapOf(
                "http_method" to request.httpMethod.toString(),
                "http_uri" to request.uri.toString(),
            )
        ) {
            logger.verbose { "custom verbose: $message" }
            logger.info { "custom info: $message" }
            logger.warn { "custom warn: $message" }
            logger.error { "custom error: $message" }
            logger.critical { "custom critical: $message" }
        }
    }

    @FunctionName("ContextLogger")
    fun handleContextLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "context-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        context.logger.fine("CONTEXT LOGGER fine: $message")
        context.logger.info { "CONTEXT LOGGER info: $message" }
        context.logger.warning("CONTEXT LOGGER warning: $message")
        context.logger.severe("CONTEXT LOGGER severe: $message")
    }

    @FunctionName("KotlinLogger")
    fun handleKotlinLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "kotlin-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        kLogger.trace { "k-logger trace: $message" }
        kLogger.debug { "k-logger debug: $message" }
        kLogger.info { "k-logger info: $message" }
        kLogger.warn { "k-logger warn: $message" }
        kLogger.error { "k-logger error: $message" }
    }

    @FunctionName("KotlinLoggerWithDimensions")
    fun handleKotlinLoggerWithDimensions(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "kotlin-logger-dimensions"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        kLogger.atTrace().addKeyValue("trace-dimension", "tracing").log { "k-logger-dimension trace: $message" }
        kLogger.atDebug().addKeyValue("debug-dimension", "debugging").log { "k-logger-dimension debug: $message" }
        kLogger.atInfo().addKeyValue("info-dimension", "informational").log { "k-logger-dimension info: $message" }
        kLogger.atWarn().addKeyValue("warn-dimension", "warned").log { "k-logger-dimension warn: $message" }
        kLogger.atError().addKeyValue("error-dimension", "erroneous").log { "k-logger-dimension error: $message" }
    }

}

val Any.logger: Logger
    get() = LoggerFactory.getLogger(this::class)
