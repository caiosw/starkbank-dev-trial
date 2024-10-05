package com.starkbank.devtrial.models

import com.starkbank.Invoice

data class InvoiceBasicRequest(
    val amount: Long,
    val taxId: String,
    val name: String,
    val tags: List<String> = listOf()
) {
    fun buildStarkInvoice(): Invoice {
        val data = HashMap<String, Any>()
        data["amount"] = amount
        data["taxId"] = taxId
        data["name"] = name
        data["tags"] = tags.toTypedArray()

        return Invoice(data)
    }
}