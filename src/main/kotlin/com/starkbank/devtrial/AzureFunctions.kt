package com.starkbank.devtrial

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import com.starkbank.Event
import com.starkbank.Settings
import com.starkbank.error.InvalidSignatureError
import com.starkbank.utils.Parse
import java.util.*

class AzureFunctions {
    init {
        StarkBankAuthenticator.authenticate()
    }

    @FunctionName("InvoiceWebhook")
    fun invoiceWebhook(
        @HttpTrigger(
            name = "InvoiceWebhook",
            route = "invoice-webhook",
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
        // TODO: Create tests for this function

        val content = request.body?.get()
        val signature = request.headers["digital-signature"]

        if (content.isNullOrEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build()
        } else {
            context.logger.info("Message body found: $content")
        }

        if (signature.isNullOrEmpty()) {
            context.logger.severe("Signature not found, headers: ${request.headers}")

            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).build()
        }

        try {
            Parse.verify<Event>(content, signature, Settings.user)
            context.logger.info("Digital-Signature verified.")

            output.value = content
            context.logger.info("New webhook event sent to service bus queue: $output")

            return request.createResponseBuilder(HttpStatus.OK).build()
        } catch (e: InvalidSignatureError) {
            context.logger.severe("Error verifying Digital-signature.")

            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            context.logger.severe("Error processing webhook: $e")

            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @FunctionName("CronTriggerFunction")
    fun cronHandler(
        @TimerTrigger(name = "cronTrigger", schedule = "0 0 */3 * * *") timerInfo: String,
        context: ExecutionContext
    ) {
        context.logger.info("Cron trigger fired at ${java.time.LocalDateTime.now()}")
    }
}
