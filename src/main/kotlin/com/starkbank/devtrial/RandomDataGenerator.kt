package com.starkbank.devtrial

import com.starkbank.devtrial.Constants.MAX_INVOICE_AMOUNT
import com.starkbank.devtrial.Constants.MAX_INVOICE_QUANTITY
import com.starkbank.devtrial.Constants.MIN_INVOICE_AMOUNT
import com.starkbank.devtrial.Constants.MIN_INVOICE_QUANTITY
import com.starkbank.devtrial.models.Person
import kotlin.random.Random

object RandomData {
    fun getInvoiceQuantity(): Int {
        return Random.nextInt(MIN_INVOICE_QUANTITY, MAX_INVOICE_QUANTITY + 1)
    }

    fun getAmount(): Long {
        return Random.nextInt(MIN_INVOICE_AMOUNT, MAX_INVOICE_AMOUNT).toLong()
    }

    fun getPerson(): Person {
        return persons[Random.nextInt(persons.size - 1)]
    }

    private val persons = listOf(
        Person(taxId = "045.463.480-37", name = "Tony Stark"),
        Person(taxId = "571.904.880-49", name = "Peter Parker"),
        Person(taxId = "726.521.490-08", name = "Pepper Potts"),
        Person(taxId = "366.872.460-11", name = "Bruce Banner"),
        Person(taxId = "939.875.830-02", name = "Steven Rogers"),
        Person(taxId = "286.814.420-90", name = "Sansa Stark"),
        Person(taxId = "237.181.800-30", name = "Arya Stark"),
        Person(taxId = "823.112.060-28", name = "Daenerys Targaryen Stormborn"),
        Person(taxId = "197.436.640-50", name = "Jon Snow"),
        Person(taxId = "418.692.440-60", name = "Tyrion Lannister")
    )
}