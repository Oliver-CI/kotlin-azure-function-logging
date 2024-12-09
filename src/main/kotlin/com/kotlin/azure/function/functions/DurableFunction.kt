package com.kotlin.azure.function.functions

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.microsoft.durabletask.RetryPolicy
import com.microsoft.durabletask.TaskOptions
import com.microsoft.durabletask.TaskOrchestrationContext
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger
import com.microsoft.durabletask.azurefunctions.DurableClientContext
import com.microsoft.durabletask.azurefunctions.DurableClientInput
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

/**
 * Please follow the below steps to run this durable function sample
 * 1. Send an HTTP GET/POST request to endpoint `StartHelloCities` to run a durable function
 * 2. Send request to statusQueryGetUri in `StartHelloCities` response to get the status of durable function
 * For more instructions, please refer https://aka.ms/durable-function-java
 *
 *
 * Please add com.microsoft:durabletask-azure-functions to your project dependencies
 * Please add `"extensions": { "durableTask": { "hubName": "JavaTestHub" }}` to your host.json
 */
@Component
class DurableFunction {
    /**
     * This HTTP-triggered function starts the orchestration.
     */
    @FunctionName("StartOrchestration")
    fun startOrchestration(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "start-durable-orchestration"
        ) request: HttpRequestMessage<Optional<String?>?>?,
        @DurableClientInput(name = "durableContext") durableContext: DurableClientContext,
        context: ExecutionContext
    ): HttpResponseMessage {
        context.logger.info("Java HTTP trigger processed a request.")

        val client = durableContext.client
        val instanceId = client.scheduleNewOrchestrationInstance("Cities")
        context.logger.info("Created new Java orchestration with instance ID = $instanceId")
        return durableContext.createCheckStatusResponse(request, instanceId)
    }

    /**
     * This is the orchestrator function, which can schedule activity functions, create durable timers,
     * or wait for external events in a way that's completely fault-tolerant.
     */
    @FunctionName("Cities")
    fun citiesOrchestrator(
        @DurableOrchestrationTrigger(name = "ctx") ctx: TaskOrchestrationContext
    ): String {
        val retryPolicy = RetryPolicy(3, Duration.ofSeconds(10))
        retryPolicy.setBackoffCoefficient(3.14)
        val taskOptions = TaskOptions(retryPolicy)

        val first = ctx.callActivity(
            "Capitalize", "New York", taskOptions,
            String::class.java
        )
        val second = ctx.callActivity(
            "Capitalize", "Tokyo", taskOptions,
            String::class.java
        )
        return ctx.allOf(listOf(first, second)).await().joinToString()
    }

    /**
     * This is the activity function that gets invoked by the orchestration.
     */
    @FunctionName("Capitalize")
    fun capitalize(
        @DurableActivityTrigger(name = "name") name: String,
        context: ExecutionContext
    ): String {
        context.logger.info("Capitalizing: $name")
        return name.uppercase(Locale.getDefault())
    }
}