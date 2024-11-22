package com.kotlin.azure.event_logger

import com.kotlin.azure.event_logger.logger.Logger
import com.kotlin.azure.event_logger.logger.LoggerFactory
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.*
import mu.withLoggingContext
import org.springframework.stereotype.Component


@Component
class LogFunction {

    @FunctionName("LogHttp")
    fun handleHttp(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "log-message"
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
            logger.verbose { "verbose: $message" }
            logger.info { "info: $message" }
            logger.warn { "warn: $message" }
            logger.error { "error: $message" }
            logger.critical { "critical: $message" }
        }
    }
}

val Any.logger: Logger
    get() = LoggerFactory.getLogger(this::class)
