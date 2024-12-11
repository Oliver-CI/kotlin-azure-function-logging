package com.kotlin.azure.function.functions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.dapr.annotation.DaprTopicTrigger
import org.springframework.stereotype.Component

@Component
class DaprFunctions {

    @FunctionName("PrintTopicMessage")
    @Throws(JsonProcessingException::class)
    fun consumeMessage(
        @DaprTopicTrigger(pubSubName = "dapr1", topic = "my-topic") payload: String?,
        context: ExecutionContext
    ): String {
        val logger = context.logger
        logger.info("Java function processed a PrintTopicMessage request from the Dapr Runtime.")

        // Get the CloudEvent data from the request body as a JSON string
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(payload)

        val data = jsonNode["data"].asText()

        logger.info("Topic my-topic received a message: $data")

        return data
    }
}