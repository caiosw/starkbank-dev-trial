package com.starkbank.devtrial.models

import com.starkbank.devtrial.DefaultEntities.defaultInvoice
import com.starkbank.devtrial.DefaultEntities.defaultInvoiceBasicRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InvoiceBasicRequestTest {
    @Test
    fun `(buildStarkInvoice) should create an Invoice as expected`() {
        val expected = defaultInvoice()

        val actual = defaultInvoiceBasicRequest.buildStarkInvoice()

        // checking strings to avoid handling with Java equality in kotlin
        assertEquals(expected.toString(), actual.toString())
    }
}