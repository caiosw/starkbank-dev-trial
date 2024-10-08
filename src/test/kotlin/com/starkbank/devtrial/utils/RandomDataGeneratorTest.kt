package com.starkbank.devtrial.utils

import com.starkbank.devtrial.Constants
import org.junit.jupiter.api.Test

class RandomDataGeneratorTest {
    @Test
    fun `(getInvoiceQuantity) should always return a random quantity inside the expected range`() {
        val expectedRange = Constants.MIN_INVOICE_QUANTITY..Constants.MAX_INVOICE_QUANTITY

        for (i in 1..100) {
            val actual = RandomData.getInvoiceQuantity()

            assert(actual in expectedRange)
        }
    }

    @Test
    fun `(getAmount) should always return a random amount inside the expected range`() {
        val expectedRange = Constants.MIN_INVOICE_AMOUNT..Constants.MAX_INVOICE_AMOUNT

        for (i in 1..100) {
            val actual = RandomData.getAmount()

            assert(actual in expectedRange)
        }
    }

    @Test
    fun `(getPerson) should return random different Persons`() {
        val persons = (1..10).map { RandomData.getPerson() }

        val differentPersons = persons.toSet()

        assert(differentPersons.size > 1)
    }
}
