package com.kotlin.azure.function.functions

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import mu.KotlinLogging
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.message.StringMapMessage
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class ContextualLoggerFunctions {
    @FunctionName("log4jMDC")
    fun handleLog4jMDC(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "log4j-mdc"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        val logger = LogManager.getLogger(this::class.java);
        MDC.put("mdc-dimension", "custom-mdc-value")
        logger.trace("log4j-mdc trace: $message")
        logger.info("log4j-mdc info: $message")
        logger.warn("log4j-mdc warn: $message")
        logger.error("log4j-mdc error: $message")
        KotlinLogging.logger { }.warn { "kotlin-logger-mdc warning: $message" }
        context.logger.warning("context-logger-mdc warning: $message")
    }

    @FunctionName("log4jThreadContext")
    fun handleLog4jThreadContext(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "log4j-thread-context"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        try {
            ThreadContext.put("log4j-thread-context-attribute", "custom-string-value")
            val message = request.body
            val logger = LogManager.getLogger(this::class.java);

            logger.trace("log4j-thread-context trace: $message")
            logger.info("log4j-thread-context info: $message")
            logger.warn("log4j-thread-context warn: $message")
            logger.error("log4j-thread-context error: $message")
            KotlinLogging.logger { }.warn { "kotlin-logger-thread-context warning: $message" }
            context.logger.warning("context-thread-context warning: $message")
        } finally {
            ThreadContext.clearAll()
        }
    }

    @FunctionName("log4jMapMessage")
    fun handleClientLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "log4j-map-message"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        val logger = LogManager.getLogger(this::class.java)

        val entry = StringMapMessage()
        entry.put("map-message-attribute", "custom-string-value")
        entry.put("message", "log4j map-message: $message")

        logger.trace(entry)
        logger.info(entry)
        logger.warn(entry)
        logger.error(entry)
    }
}