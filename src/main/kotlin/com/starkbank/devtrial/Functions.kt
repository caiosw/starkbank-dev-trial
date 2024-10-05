package com.starkbank.devtrial

import com.microsoft.azure.functions.annotation.*
import com.microsoft.azure.functions.*
import java.util.Optional

class Functions {

    @FunctionName("HttpTriggerFunction")
    fun httpHandler(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "test"
        ) request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext
    ): HttpResponseMessage {
        val name = request.queryParameters["name"] ?: request.body.orElse("world")

        val responseMessage = "Hello, $name"

        context.logger.info("responseMessage: $responseMessage")
        return request.createResponseBuilder(HttpStatus.OK).body(responseMessage).build()
    }

    @FunctionName("CronTriggerFunction")
    fun cronHandler(
        @TimerTrigger(name = "cronTrigger", schedule = "0 */30 * * * *") timerInfo: String,
        context: ExecutionContext
    ) {
        context.logger.info("Cron trigger fired at ${java.time.LocalDateTime.now()}")
    }
}
