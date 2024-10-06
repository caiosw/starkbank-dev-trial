package com.starkbank.devtrial

import com.microsoft.azure.functions.annotation.*
import com.microsoft.azure.functions.*
import java.util.Optional

class AzureFunctions {
    @FunctionName("InvoiceWebhook")
    fun invoiceWebhook(
        @HttpTrigger(
            name = "InvoiceWebhook",
            route = "invoice-webhook",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS
        ) request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext,
        @ServiceBusQueueOutput(
            name = "ServiceBusOutputMessage",
            queueName = "dev-starkbank-devtrial-sbq",
            connection = "SERVICE_BUS_CONN_STRING"
        ) output: OutputBinding<String>
    ): HttpResponseMessage {
        val message = request.body.orElse("I received a webhook")
        context.logger.info("Received message: $message")

        // Process the message
        val processedMessage = "Processed: $message"

        // Send the processed message to Service Bus
        output.value = processedMessage
        context.logger.info("Message sent to Service Bus: $processedMessage")

        // Return a 200 response for the webhook sender
        return request.createResponseBuilder(HttpStatus.OK).body("Message sent to the queue").build()
    }

    @FunctionName("CronTriggerFunction")
    fun cronHandler(
        @TimerTrigger(name = "cronTrigger", schedule = "0 */30 * * * *") timerInfo: String,
        context: ExecutionContext
    ) {
        context.logger.info("Cron trigger fired at ${java.time.LocalDateTime.now()}")
    }
}
