package com.starkbank.devtrial

import com.starkbank.devtrial.DefaultEntities.defaultAmount
import com.starkbank.devtrial.DefaultEntities.defaultInvoice
import com.starkbank.devtrial.DefaultEntities.defaultPerson
import com.starkbank.devtrial.models.Person
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verifySequence
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InvoiceBuilderTest {
    @BeforeEach
    fun setUp() {
        mockkObject(RandomData)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(RandomData)
    }

    @Test
    fun `(buildMany) should build a random quantity of invoices as expected`() {
        val expectedQuantity = 3

        val mockedPersons = listOf(
            Person(taxId = "111.111.111-11", name = "Name 1"),
            Person(taxId = "222.222.222-22", name = "Name 2"),
            Person(taxId = "333.333.333-33", name = "Name 3")
        )

        val mockedAmounts = listOf(100L, 200L, 300L)

        every { RandomData.getPerson() } returnsMany mockedPersons
        every { RandomData.getAmount() } returnsMany mockedAmounts

        val actual = InvoiceBuilder.buildMany(expectedQuantity)

        assertEquals(expectedQuantity, actual.size)

        verifySequence {
            RandomData.getPerson()
            RandomData.getAmount()
            RandomData.getPerson()
            RandomData.getAmount()
            RandomData.getPerson()
            RandomData.getAmount()
        }
    }

    @Test
    fun `(buildOne) should build a single invoice as expected`() {
        val expected = defaultInvoice()

        every { RandomData.getPerson() } returns defaultPerson
        every { RandomData.getAmount() } returns defaultAmount

        val actual = InvoiceBuilder.buildOne()

        // checking strings to avoid handling with Java equality in kotlin
        assertEquals(expected.toString(), actual.toString())

        verifySequence {
            RandomData.getPerson()
            RandomData.getAmount()
        }
    }
}