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

    @FunctionName("LogEvent")
    fun handleEvent(
        @ServiceBusTopicTrigger(
            name = "message",
            topicName = "log-topic",
            subscriptionName = "sub-event-logger",
            connection = "AzureWebJobsServiceBus"
        ) message: String,
        @BindingName("CorrelationId") correlationId: String,
        @BindingName("MessageId") messageId: String,
        @BindingName("Subject") subject: String,
        @BindingName("ApplicationProperties") applicationProperties: Map<String, Any>,
        context: ExecutionContext
    ) {
        withLoggingContext(
            mapOf(
                "CorrelationId" to correlationId,
                "MessageId" to messageId,
                "ProcessId" to applicationProperties["process-id"]?.toString()
            )
        ) {
            logger.info { "Function LogEvent called" }
            logger.info(mapOf("Subject" to subject)) { "Received event" }
        }
        logger.info { "Function LogEvent called" }
    }

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
