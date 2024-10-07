package com.starkbank.devtrial

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.OutputBinding
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.Cardinality
import com.microsoft.azure.functions.annotation.FixedDelayRetry
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.microsoft.azure.functions.annotation.ServiceBusQueueOutput
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger
import com.microsoft.azure.functions.annotation.TimerTrigger
import com.starkbank.devtrial.azf.StarkWebhookFunction
import com.starkbank.devtrial.azf.WebhookEventServiceBusConsumerFunction
import java.util.Optional

class AzureFunctions {
    init {
        StarkBankAuthenticator.authenticate()
    }

    @FunctionName("StarkWebhook")
    fun starkWebhook(
        @HttpTrigger(
            name = "StarkWebhook",
            route = "stark-webhook",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS
        ) request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext,
        @ServiceBusQueueOutput(
            name = "ServiceBusOutputMessage",
            queueName = "dev-starkbank-devtrial-sbq",
            connection = "SERVICE_BUS_CONN_STRING"
        ) output: OutputBinding<String>
    ): HttpResponseMessage {
        return StarkWebhookFunction.run(request, context, output)
    }

    @FunctionName("WebhookEventServiceBusConsumer")
    fun webhookEventServiceBusConsumer(
        @ServiceBusQueueTrigger(
            name = "WebhookEventServiceBusConsumer",
            queueName = "dev-starkbank-devtrial-sbq",
            connection = "SERVICE_BUS_CONN_STRING",
            cardinality = Cardinality.ONE,
        ) message: String,
        context: ExecutionContext
    ) {
        WebhookEventServiceBusConsumerFunction.run(message, context)
    }

    @FunctionName("CronTriggerFunction")
    @FixedDelayRetry(maxRetryCount = 10, delayInterval = "00:00:30")
    fun cronHandler(
        @TimerTrigger(name = "cronTrigger", schedule = "0 0 */3 * * *") timerInfo: String,
        context: ExecutionContext
    ) {
        context.logger.info("Cron trigger fired at ${java.time.LocalDateTime.now()}")
    }
}
