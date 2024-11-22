package com.kotlin.azure.function.functions

import com.azure.core.util.logging.ClientLogger
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

    @FunctionName("ClientLogger")
    fun handleClientLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "client-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        val logger = ClientLogger(this::class.java)
        logger.atVerbose().addKeyValue("verbose-dimension", "very verbose").log {"client logger verbose: $message"}
        logger.atInfo().addKeyValue("info-dimension", "informational").log { "client logger info: $message" }
        logger.atWarning().addKeyValue("warn-dimension", "warned").log { "client logger warn: $message" }
        logger.atError().addKeyValue("error-dimension", "erroneous").log { "client logger error: $message" }
    }

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

    @FunctionName("ContextLoggerWithDimensions")
    fun handleContextLoggerWithDimensions(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "context-logger-dimensions"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        context.traceContext.attributes["custom-attribute"] = "custom-value"
        context.logger.info { "CONTEXT LOGGER traceState: ${context.traceContext.tracestate}" }
        context.logger.fine("CONTEXT LOGGER fine: $message")
        context.logger.info { "CONTEXT LOGGER info: $message" }
        context.logger.warning("CONTEXT LOGGER warning: $message")
        context.logger.severe("CONTEXT LOGGER severe: $message")
        // map is immutable
        context.traceContext.attributes["custom-attribute"] = "custom-value"
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
