package com.starkbank.devtrial

import java.time.LocalDateTime

object Constants {
    const val MIN_INVOICE_QUANTITY = 8
    const val MAX_INVOICE_QUANTITY = 12
    const val MIN_INVOICE_AMOUNT = 0
    const val MAX_INVOICE_AMOUNT = 1000000
    const val PROJECT_ID = "5473382518751232"
    const val ENV = "sandbox"
    val INVOICE_TAG_LIST = listOf("devtrial")
    val TRANSFER_TAG_LIST = listOf("devtrial")
    val LAST_SCHEDULED_DATE_TIME = LocalDateTime.of(2024, 10, 9, 3, 0, 0)
}