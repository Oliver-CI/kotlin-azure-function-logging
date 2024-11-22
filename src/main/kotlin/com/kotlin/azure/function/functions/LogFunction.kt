package com.kotlin.azure.function.functions

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.trace.Span
import mu.KotlinLogging
import org.springframework.stereotype.Component

val kLogger = KotlinLogging.logger {}

@Component
class LogFunction {
    @FunctionName("ContextLoggerSpanned")
    fun handleContextLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "context-logger-spanned"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val span = Span.current()
        val attributeKey = AttributeKey.stringKey("custom-string-attribute")
        span.setAttribute(attributeKey, "custom-string-value")
        span.setAttribute("another-custom-attribute", 42)
        val message = request.body
        context.logger.fine("CONTEXT LOGGER fine: $message")
        context.logger.info { "CONTEXT LOGGER info: $message" }
        context.logger.warning("CONTEXT LOGGER warning: $message")
        context.logger.severe("CONTEXT LOGGER severe: $message")
        kLogger.info { "KOTLIN LOGGER info: $message" }
    }
}
