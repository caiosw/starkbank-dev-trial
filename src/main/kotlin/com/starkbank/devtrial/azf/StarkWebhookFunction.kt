package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.*
import com.starkbank.Event
import com.starkbank.Settings
import com.starkbank.error.InvalidSignatureError
import com.starkbank.utils.Parse
import java.util.*

object StarkWebhookFunction {
    fun run(
        request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext,
        output: OutputBinding<String>
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
}