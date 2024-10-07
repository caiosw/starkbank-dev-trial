package com.starkbank.devtrial

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import com.starkbank.devtrial.azf.StarkWebhookFunction
import com.starkbank.devtrial.azf.WebhookEventServiceBusConsumerFunction
import java.util.*


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
        context.logger.warning("testing sending transfer!! (2)")
        WebhookEventServiceBusConsumerFunction.run(message, context)
    }

    @FunctionName("CronTriggerFunction")
    fun cronHandler(
        @TimerTrigger(name = "cronTrigger", schedule = "0 0 */3 * * *") timerInfo: String,
        context: ExecutionContext
    ) {
        context.logger.info("Cron trigger fired at ${java.time.LocalDateTime.now()}")
    }
}
