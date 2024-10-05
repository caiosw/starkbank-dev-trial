package com.starkbank.devtrial

import com.starkbank.Invoice
import com.starkbank.devtrial.models.InvoiceBasicRequest
import com.starkbank.devtrial.models.Person

object DefaultEntities {
    val defaultAmount = 1000L
    val defaultTaxId = "12345678901"
    val defaultName = "Test Name"

    val defaultInvoiceBasicRequest = InvoiceBasicRequest(
        amount = defaultAmount,
        taxId = defaultTaxId,
        name = defaultName,
        tags = Constants.INVOICE_TAG_LIST
    )

    val defaultPerson = Person(
        taxId = defaultTaxId,
        name = defaultName
    )

    fun defaultInvoice(): Invoice {
        val data = HashMap<String, Any>()
        data["amount"] = defaultAmount
        data["taxId"] = defaultTaxId
        data["name"] = defaultName
        data["tags"] = Constants.INVOICE_TAG_LIST.toTypedArray()

        return Invoice(data)
    }
}