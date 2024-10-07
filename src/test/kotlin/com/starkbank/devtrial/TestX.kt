package com.starkbank.devtrial

import com.starkbank.Invoice
import org.junit.jupiter.api.Test


class TestX {

//    @Test
    fun Potato() {
        StarkBankAuthenticator.authenticate()

        val invoices = InvoiceBuilder.buildMany(3)

        val x = Invoice.create(invoices)

        x.forEach { println(it.toString()) }
    }


}