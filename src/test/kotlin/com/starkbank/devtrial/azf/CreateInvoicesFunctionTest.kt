package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.ExecutionContext
import com.starkbank.Invoice
import com.starkbank.devtrial.DefaultEntities.defaultInvoiceBasicRequest
import com.starkbank.devtrial.InvoiceBuilder
import com.starkbank.devtrial.RandomData
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verifySequence
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.logging.Logger


class CreateInvoicesFunctionTest {
    @BeforeEach
    fun setUp() {
        mockkObject(RandomData)
        mockkObject(InvoiceBuilder)
        mockkStatic(Invoice::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(RandomData)
        unmockkObject(InvoiceBuilder)
        unmockkStatic(Invoice::class)
    }

    @Test
    fun `(run) should create a random quantity of invoices as expected`() {
        val quantity = 4
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()

        val invoices = listOf(
            defaultInvoiceBasicRequest.copy(amount = 100),
            defaultInvoiceBasicRequest.copy(amount = 200),
            defaultInvoiceBasicRequest.copy(amount = 300),
            defaultInvoiceBasicRequest.copy(amount = 400)
        ).map { it.buildStarkInvoice() }

        every { RandomData.getInvoiceQuantity() } returns quantity

        every { InvoiceBuilder.buildMany(quantity) } returns invoices

        every { Invoice.create(invoices) } returns invoices

        every { contextMock.logger } returns loggerMock

        for (invoice in invoices) {
            every { loggerMock.fine("Invoice created: $invoice") } just Runs
        }

        CreateInvoicesFunction.run(contextMock)

        verifySequence {
            RandomData.getInvoiceQuantity()
            InvoiceBuilder.buildMany(quantity)
            Invoice.create(invoices)
            for (invoice in invoices) {
                contextMock.logger
                loggerMock.fine("Invoice created: $invoice")
            }
        }
    }

    @Test
    fun `(run) should throw an error when something goes wrong during Invoice creation`() {
        val quantity = 2
        val contextMock = mockk<ExecutionContext>()
        val loggerMock = mockk<Logger>()

        val invoices = listOf(
            defaultInvoiceBasicRequest.copy(amount = 100),
            defaultInvoiceBasicRequest.copy(amount = 200)
        ).map { it.buildStarkInvoice() }

        every { RandomData.getInvoiceQuantity() } returns quantity

        every { InvoiceBuilder.buildMany(quantity) } returns invoices

        every { Invoice.create(invoices) } throws Exception("Some error occurred.")

        every { contextMock.logger } returns loggerMock

        every { loggerMock.severe("Error while creating invoices!") } just Runs

        assertThrows<Exception> {
            CreateInvoicesFunction.run(contextMock)
        }

        verifySequence {
            RandomData.getInvoiceQuantity()
            InvoiceBuilder.buildMany(quantity)
            Invoice.create(invoices)
            contextMock.logger
            loggerMock.severe("Error while creating invoices!")
        }
    }
}