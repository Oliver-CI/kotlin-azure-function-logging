package com.kotlin.azure.function.functions

import com.azure.core.util.logging.ClientLogger
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.Span
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class SpanFunction {
    @FunctionName("LoggingWithSpan")
    fun handleClientLogger(
        @HttpTrigger(
            name = "request",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "spanned-loggers"
        ) request: HttpRequestMessage<String>,
        context: ExecutionContext
    ) {
        val message = request.body
        val clientLogger = ClientLogger(this::class.java)
        val contextLogger = context.logger
        val kotlinLogger = KotlinLogging.logger {}

        val span = Span.current()
        val attributeKey = AttributeKey.stringKey("custom-string-attribute")
        span.setAttribute(attributeKey, "custom-string-value")
        span.setAttribute("another-custom-attribute", 42)

        clientLogger.warning("client-logger warning: $message")
        contextLogger.warning("context-logger warning: $message")
        kotlinLogger.warn { "kotlin-logger warning: $message" }
    }
}