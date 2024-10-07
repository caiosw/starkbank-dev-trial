package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.*
import com.starkbank.Event
import com.starkbank.Settings
import com.starkbank.devtrial.DefaultEntities.defaultWebhookMessageBody
import com.starkbank.error.InvalidSignatureError
import com.starkbank.utils.Parse
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.logging.Logger

class StarkWebhookFunctionTest {
    private val defaultSignature = "signature"
    private val defaultHeaders = mapOf("digital-signature" to defaultSignature)
    private val defaultMessageBody = defaultWebhookMessageBody()

    @BeforeEach
    fun setUp() {
        mockkStatic(Parse::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Parse::class)
    }

    @Test
    fun `(run) should send a valid webhook from StarkBank to the ServiceBus queue as expected`() {
        val requestMock = mockk<HttpRequestMessage<Optional<String>>>()
        val responseBuilderMock = mockk<HttpResponseMessage.Builder>()
        val responseMock = mockk<HttpResponseMessage>()
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()
        val outputMock = mockk<OutputBinding<String>>()

        every { requestMock.body } returns Optional.of(defaultWebhookMessageBody())
        every { requestMock.headers } returns defaultHeaders

        every { Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user) } returns ""

        every { contextMock.logger } returns loggerMock
        every { loggerMock.info("Digital-Signature verified.") } just Runs
        every {
            loggerMock.info("New webhook event sent to service bus queue: $defaultMessageBody")
        } just Runs

        every { requestMock.createResponseBuilder(HttpStatus.OK) } returns responseBuilderMock
        every { responseBuilderMock.build() } returns responseMock

        every { outputMock.setValue(defaultMessageBody) } just Runs


        StarkWebhookFunction.run(requestMock, contextMock, outputMock)


        verifySequence {
            requestMock.body
            requestMock.headers
            Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user)
            contextMock.logger
            loggerMock.info("Digital-Signature verified.")
            outputMock.setValue(defaultMessageBody)
            contextMock.logger
            loggerMock.info("New webhook event sent to service bus queue: $defaultMessageBody")
            requestMock.createResponseBuilder(HttpStatus.OK)
            responseBuilderMock.build()
        }
    }

    @Test
    fun `(run) should respond a request without body with BAD_REQUEST error code`() {
        val requestMock = mockk<HttpRequestMessage<Optional<String>>>()
        val responseBuilderMock = mockk<HttpResponseMessage.Builder>()
        val responseMock = mockk<HttpResponseMessage>()
        val contextMock = mockk<ExecutionContext>()
        val outputMock = mockk<OutputBinding<String>>()

        every { requestMock.body } returns null

        every { requestMock.createResponseBuilder(HttpStatus.BAD_REQUEST) } returns responseBuilderMock
        every { responseBuilderMock.build() } returns responseMock

        StarkWebhookFunction.run(requestMock, contextMock, outputMock)

        verifySequence {
            requestMock.body
            requestMock.createResponseBuilder(HttpStatus.BAD_REQUEST)
            responseBuilderMock.build()
        }
    }

    @Test
    fun `(run) should return UNAUTHORIZED error code if the digital-signature was not found`() {
        val headers = mapOf("other-header" to "other value")

        val requestMock = mockk<HttpRequestMessage<Optional<String>>>()
        val responseBuilderMock = mockk<HttpResponseMessage.Builder>()
        val responseMock = mockk<HttpResponseMessage>()
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()
        val outputMock = mockk<OutputBinding<String>>()

        every { requestMock.body } returns Optional.of(defaultMessageBody)
        every { requestMock.headers } returns headers

        every { contextMock.logger } returns loggerMock
        every { loggerMock.severe("Signature not found. headers = $headers") } just Runs

        every { requestMock.createResponseBuilder(HttpStatus.UNAUTHORIZED) } returns responseBuilderMock
        every { responseBuilderMock.build() } returns responseMock

        StarkWebhookFunction.run(requestMock, contextMock, outputMock)

        verifySequence {
            requestMock.body
            requestMock.headers
            contextMock.logger
            requestMock.headers
            loggerMock.severe("Signature not found. headers = $headers")
            requestMock.createResponseBuilder(HttpStatus.UNAUTHORIZED)
            responseBuilderMock.build()
        }
    }

    @Test
    fun `(run) should return a UNAUTHORIZED error code if the digital-signature is not validated`() {
        val requestMock = mockk<HttpRequestMessage<Optional<String>>>()
        val responseBuilderMock = mockk<HttpResponseMessage.Builder>()
        val responseMock = mockk<HttpResponseMessage>()
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()
        val outputMock = mockk<OutputBinding<String>>()

        every { requestMock.body } returns Optional.of(defaultMessageBody)
        every { requestMock.headers } returns defaultHeaders

        every {
            Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user)
        } throws InvalidSignatureError("")

        every { contextMock.logger } returns loggerMock
        every { loggerMock.severe("Error verifying Digital-signature.") } just Runs

        every { requestMock.createResponseBuilder(HttpStatus.UNAUTHORIZED) } returns responseBuilderMock
        every { responseBuilderMock.build() } returns responseMock


        StarkWebhookFunction.run(requestMock, contextMock, outputMock)


        verifySequence {
            requestMock.body
            requestMock.headers
            Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user)
            contextMock.logger
            loggerMock.severe("Error verifying Digital-signature.")
            requestMock.createResponseBuilder(HttpStatus.UNAUTHORIZED)
            responseBuilderMock.build()
        }
    }

    @Test
    fun `(run) should return a INTERNAL_SERVER_ERROR error when any other unexpected error happen`() {
        val requestMock = mockk<HttpRequestMessage<Optional<String>>>()
        val responseBuilderMock = mockk<HttpResponseMessage.Builder>()
        val responseMock = mockk<HttpResponseMessage>()
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()
        val outputMock = mockk<OutputBinding<String>>()

        val exceptionMessageSlot = slot<String>()

        every { requestMock.body } returns Optional.of(defaultMessageBody)
        every { requestMock.headers } returns defaultHeaders

        every { Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user) } throws Exception("")

        every { contextMock.logger } returns loggerMock
        every { loggerMock.severe(capture(exceptionMessageSlot)) } just Runs

        every { requestMock.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR) } returns responseBuilderMock
        every { responseBuilderMock.build() } returns responseMock


        StarkWebhookFunction.run(requestMock, contextMock, outputMock)


        assert(exceptionMessageSlot.captured.contains("Error processing webhook:"))

        verifySequence {
            requestMock.body
            requestMock.headers
            Parse.verify<Event>(defaultMessageBody, defaultSignature, Settings.user)
            contextMock.logger
            loggerMock.severe(exceptionMessageSlot.captured)
            requestMock.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            responseBuilderMock.build()
        }
    }
}