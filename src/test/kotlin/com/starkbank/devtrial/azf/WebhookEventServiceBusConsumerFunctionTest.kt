package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.*
import com.starkbank.Transfer
import com.starkbank.devtrial.DefaultEntities.defaultInvoiceEvent
import com.starkbank.devtrial.DefaultEntities.defaultWebhookMessageBody
import com.starkbank.devtrial.createTransferToStarkBank
import com.starkbank.devtrial.exceptions.EventTypeNotImplementedException
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.logging.Logger


class WebhookEventServiceBusConsumerFunctionTest {
    @BeforeEach
    fun setUp() {
        mockkStatic(Transfer::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Transfer::class)
    }

    @Test
    fun `(run) should consume a invoice event that was paid and create a Transfer`() {
        val transfer = defaultInvoiceEvent.log.invoice.createTransferToStarkBank()
        val transferList = listOf(transfer)

        val transferListSlot = slot<List<Transfer>>()

        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()

        every { Transfer.create(capture(transferListSlot)) } returns transferList

        every { contextMock.logger } returns loggerMock
        every { loggerMock.info("Transfer created: $transferList") } just Runs


        WebhookEventServiceBusConsumerFunction.run(defaultWebhookMessageBody(), contextMock)


        assertEquals(transferList.toString(), transferListSlot.captured.toString())

        verifySequence {
            Transfer.create(transferListSlot.captured)
            loggerMock.info("Transfer created: $transferList")
        }
    }

    @Test
    fun `(run) should ignore a invoice event of type created`() {
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()

        every { contextMock.logger } returns loggerMock
        every {
            loggerMock.fine("For the purpose of this application other event types will be ignored.")
        } just Runs


        WebhookEventServiceBusConsumerFunction.run(defaultWebhookMessageBody("created"), contextMock)


        verifySequence {
            loggerMock.fine("For the purpose of this application other event types will be ignored.")
        }
    }

    @Test
    fun `(run) should throw an error if the event subscription type was not implemented`() {
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()

        val severeLogMessage = slot<String>()

        every { contextMock.logger } returns loggerMock

        every {
            loggerMock.severe(capture(severeLogMessage))
        } just Runs

        assertThrows<EventTypeNotImplementedException> {
            WebhookEventServiceBusConsumerFunction.run(
                defaultWebhookMessageBody(subscriptionType = "boleto"),
                contextMock
            )
        }

        assert(severeLogMessage.captured.contains("Exception found while processing event message:"))

        verifySequence {
            loggerMock.severe(severeLogMessage.captured)
        }
    }
}