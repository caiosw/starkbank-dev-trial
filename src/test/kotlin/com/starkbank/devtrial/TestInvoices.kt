package com.starkbank.devtrial

import com.starkbank.Invoice
import com.starkbank.devtrial.utils.InvoiceBuilder

class TestInvoices {

//    @Test
    fun createInvoices() {
        StarkBankAuthenticator.authenticate()

        val invoices = InvoiceBuilder.buildMany(3)

        val x = Invoice.create(invoices)

        x.forEach { println(it.toString()) }
    }
}
