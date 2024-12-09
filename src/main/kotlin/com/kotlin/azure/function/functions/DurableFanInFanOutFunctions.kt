package com.kotlin.azure.function.functions

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.microsoft.durabletask.TaskOrchestrationContext
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger
import com.microsoft.durabletask.azurefunctions.DurableClientContext
import com.microsoft.durabletask.azurefunctions.DurableClientInput
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger
import com.microsoft.durabletask.interruption.OrchestratorBlockedException
import org.springframework.stereotype.Component
import java.util.*

@Component
class DurableFanInFanOutFunctions {

    @FunctionName("StartFanInFanOutOrchestration")
    fun startFanInFanOutOrchestration(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "start-fan-in-fan-out-orchestration"
        ) request: HttpRequestMessage<Optional<String?>?>?,
        @DurableClientInput(name = "durableContext") durableContext: DurableClientContext,
        context: ExecutionContext
    ): HttpResponseMessage {
        context.logger.info("Java HTTP trigger processed a request.")

        val client = durableContext.client
        val instanceId = client.scheduleNewOrchestrationInstance("FanInFanOutOrchestrator")
        context.logger.info("Created new Java orchestration with instance ID = $instanceId")
        return durableContext.createCheckStatusResponse(request, instanceId)
    }

    @FunctionName("FanInFanOutOrchestrator")
    fun fanInFanOutOrchestrator(
        @DurableOrchestrationTrigger(name = "ctx") ctx: TaskOrchestrationContext,
        context: ExecutionContext
    ): Long {
        var results: List<Result<*>> = emptyList()
        try {
            results = listOf(43L, 44L, 45L, 46L)
                .map { input -> ctx.callActivity("FibonacciActivity", input, Result::class.java) }
                .map { it.await() }
        } catch (e: OrchestratorBlockedException) {
            // This exception is expected when the orchestrator is blocked
            throw e
        } catch (e: Exception) {
            context.logger.severe("${e.javaClass.name} in fanInFanOutOrchestrator: ${e.message}")
        }

        val result = results
            .filter { it.isSuccess }
            .map { it.getOrNull() as Long }
            .sum()

        context.logger.info { "Sum of Fibonacci results: $result" }
        return result
    }

    @FunctionName("FibonacciActivity")
    fun fanOutActivity(
        @DurableActivityTrigger(name = "input") n: Long,
        context: ExecutionContext
    ): Result<Long> {
        context.logger.info("Executing FibonacciActivity with input: $n")
        if (n == 45L) {
            throw RuntimeException("45 not allowed")
        }
        if (n == 46L) {
            return Result.failure(RuntimeException("46 not allowed"))
        }
        return Result.success(fibonacci(n))
    }
}

private fun fibonacci(n: Long): Long = if (n <= 1) n else fibonacci(n - 1) + fibonacci(n - 2)
