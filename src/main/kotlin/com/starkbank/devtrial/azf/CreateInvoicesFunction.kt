package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.ExecutionContext
import com.starkbank.Invoice
import com.starkbank.devtrial.utils.InvoiceBuilder
import com.starkbank.devtrial.utils.RandomData

object CreateInvoicesFunction {
    fun run(context: ExecutionContext) {
        val quantity = RandomData.getInvoiceQuantity()

        val invoices = InvoiceBuilder.buildMany(quantity)

        try {
            val createdInvoices = Invoice.create(invoices)

            createdInvoices.forEach { newInvoice ->
                context.logger.fine("Invoice created: $newInvoice")
            }
        } catch (e: Exception) {
            context.logger.severe("Error while creating invoices!")
            throw e
        }
    }
}