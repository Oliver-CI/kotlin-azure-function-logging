package com.kotlin.azure.event_logger

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.*
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.stereotype.Component

val logger = KotlinLogging.logger {}

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
            logger.info { "TEST SANDER2" }
            logger.info(mapOf("Subject" to subject)) { "Received Sivi event" }
        }
        logger.info { "TEST SANDER3" }
    }

    @FunctionName("LogHttp")
    fun handleHttp(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.GET],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/log-level"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
            logger.trace { "" }
            logger.debug { "" }
            logger.info { "" }
            logger.warn { "" }
            logger.error { "" }
    }

}