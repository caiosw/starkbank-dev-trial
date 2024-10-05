package com.starkbank.devtrial

import com.starkbank.Invoice
import com.starkbank.devtrial.models.InvoiceBasicRequest

object InvoiceBuilder {
    fun buildMany(quantity: Int): List<Invoice> {
        val invoices = (0 until quantity).map {
            buildOne()
        }

        return invoices
    }

    fun buildOne(): Invoice {
        val person = RandomData.getPerson()
        val amount = RandomData.getAmount()

        val invoiceBasicRequest = InvoiceBasicRequest(
            amount = amount,
            taxId = person.taxId,
            name = person.name,
            tags = Constants.INVOICE_TAG_LIST
        )

        return invoiceBasicRequest.buildStarkInvoice()
    }
}