package com.starkbank.devtrial.azf

import com.google.gson.JsonSyntaxException
import com.microsoft.azure.functions.ExecutionContext
import com.starkbank.Event
import com.starkbank.Transfer
import com.starkbank.devtrial.WebhookEventParser
import com.starkbank.devtrial.createTransferToStarkBank
import com.starkbank.devtrial.exceptions.EventTypeNotFoundException
import com.starkbank.devtrial.exceptions.EventTypeNotImplementedException
import com.starkbank.devtrial.exceptions.StarkTrialException

object WebhookEventServiceBusConsumerFunction {
    fun run(message: String, context: ExecutionContext) {
        try {
            val event = WebhookEventParser.parseEvent(message)

            when (event.subscription) {
                "invoice" -> {
                    val invoiceEvent = event as Event.InvoiceEvent

                    if (invoiceEvent.log.type == "paid") {
                        val transfer = invoiceEvent.log.invoice.createTransferToStarkBank()

                        val createdTransfer = Transfer.create(listOf(transfer))
                        context.logger.info("Transfer created: $createdTransfer")
                    } else {
                        context.logger.fine(
                            "For the purpose of this application other event types will be ignored."
                        )
                    }
                }
                "transfer",
                "boleto",
                "boleto-payment",
                "utility-payment",
                "tax-payment",
                "boleto-holmes",
                "brcode-payment",
                "deposit" -> throw EventTypeNotImplementedException(
                    "Not ready to process event of type '${event.subscription}'"
                )

                else -> throw EventTypeNotFoundException(
                    "Event of type '${event.subscription}' not identified."
                )
            }
        } catch (e: StarkTrialException) {
            /*
                The idea here was to use the custom Exceptions that inherit from StarkTrialException to send the
                messages to the dead letter with a defined reason. The issue found is that the Azure Function lib
                for java don't allow us to receive the message as a ServiceBusReceivedMessage like in C#, we can
                only receive it as a String. That way we don't have access to the object needed to send a message
                to the dead letter. Just throwing an error returns the message to the queue until the ServiceBus
                limit so it get thrown in the dead letter by the ServiceBus itself.
                TODO: If there's time, move this function to a consumer in an Azure Container App so we can
                  handle the queue in a more efficient way.
            */
//            val deadLetterOptions = DeadLetterOptions()
//                .setDeadLetterReason(e.getFailureReason().name)
//                .setDeadLetterErrorDescription(e.message)
//            serviceBusReceiverClient.deadLetter(message, deadLetterOptions)

            context.logger.severe("Exception found while processing event message: $e")
            throw e
        } catch (e: JsonSyntaxException) {
//            val deadLetterOptions = DeadLetterOptions()
//                .setDeadLetterReason(StarkTrialExceptionFailureReason.INVALID_JSON_FORMAT.name)
//                .setDeadLetterErrorDescription(e.message)
//            serviceBusReceiverClient.deadLetter(message, deadLetterOptions)

            context.logger.severe("Exception found while processing event message: $e")
            throw e
        } catch (e: Exception) {
            context.logger.severe("Unknown exception found while processing event message: $e")
            throw Exception("Unhandled exception: $e")
        }
    }
}