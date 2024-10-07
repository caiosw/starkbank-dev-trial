package com.starkbank.devtrial

import com.starkbank.Transfer
import com.starkbank.devtrial.DefaultEntities.defaultInvoiceEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtensionsTest {
    @Test
    fun `(createTransferToStarkBank) should convert an Invoice to a Transfer to Stark Bank correctly`() {
        val actual = defaultInvoiceEvent.log.invoice.createTransferToStarkBank()

        val amount = defaultInvoiceEvent.log.invoice.amount.toLong() - defaultInvoiceEvent.log.invoice.fee.toLong()
        val data = HashMap<String, Any>()
        data["amount"] = amount
        data["taxId"] = "20.018.183/0001-80"
        data["name"] = "Stark Bank S.A."
        data["bankCode"] = "20018183"
        data["branchCode"] = "0001"
        data["accountNumber"] = "6341320293482496"
        data["accountType"] = "payment"
        data["tags"] = Constants.TRANSFER_TAG_LIST.toTypedArray()

        val expected = Transfer(data)

        assertEquals(expected.toString(), actual.toString())
    }
}