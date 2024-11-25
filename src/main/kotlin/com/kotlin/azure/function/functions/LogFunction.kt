package com.kotlin.azure.function.functions

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.stereotype.Component

val kLogger = KotlinLogging.logger {}

private const val MDC_KEY = "custom-attribute"

@Component
class LogFunction {

    @FunctionName("JavaLogger")
    fun handleJavaLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "java-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        val logger = java.util.logging.Logger.getLogger(this::class.java.name)
        withLoggingContext(
            mapOf(
                MDC_KEY to "java-logger"
            )
        ) {
            logger.fine("java logger fine: $message")
            logger.info("java logger info: $message")
            logger.warning("java logger warn: $message")
            logger.severe("java logger error: $message")
        }
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
        withLoggingContext(
            mapOf(
                MDC_KEY to "kotlin-logger"
            )
        ) {
            kLogger.trace { "k-logger trace: $message" }
            kLogger.debug { "k-logger debug: $message" }
            kLogger.info { "k-logger info: $message" }
            kLogger.warn { "k-logger warn: $message" }
            kLogger.error { "k-logger error: $message" }
        }
    }


    @FunctionName("ContextLoggerWithDimensions")
    fun handleContextLoggerWithDimensions(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "context-logger"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        withLoggingContext(
            mapOf(
                MDC_KEY to "context-logger"
            )
        ) {
            context.logger.fine("CONTEXT LOGGER fine: $message")
            context.logger.info { "CONTEXT LOGGER info: $message" }
            context.logger.warning("CONTEXT LOGGER warning: $message")
            context.logger.severe("CONTEXT LOGGER severe: $message")
        }
    }
}
