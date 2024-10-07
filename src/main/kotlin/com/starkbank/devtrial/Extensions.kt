package com.starkbank.devtrial

import com.starkbank.Invoice
import com.starkbank.Transfer

fun Invoice.createTransferToStarkBank(): Transfer {
    val amount = this.amount.toLong() - this.fee.toLong()

    val data = HashMap<String, Any>()
    data["amount"] = amount
    data["taxId"] = "20.018.183/0001-80"
    data["name"] = "Stark Bank S.A."
    data["bankCode"] = "20018183"
    data["branchCode"] = "0001"
    data["accountNumber"] = "6341320293482496"
    data["accountType"] = "payment"
    data["tags"] = Constants.TRANSFER_TAG_LIST.toTypedArray()

    return Transfer(data)
}