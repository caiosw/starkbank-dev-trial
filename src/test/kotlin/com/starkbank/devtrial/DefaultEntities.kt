package com.starkbank.devtrial

import com.starkbank.Event
import com.starkbank.Invoice
import com.starkbank.devtrial.models.InvoiceBasicRequest
import com.starkbank.devtrial.models.Person
import com.starkbank.devtrial.utils.WebhookEventParser

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

    fun defaultWebhookMessageBody(logType: String = "paid", subscriptionType: String = "invoice") = """
        {
          "event": {
            "created": "2024-10-06T05:20:20.331385+00:00",
            "id": "10",
            "log": {
              "created": "2024-10-06T05:20:14.070484+00:00",
              "errors": [],
              "id": "10",
              "invoice": {
                "amount": 431745,
                "brcode": "brcode",
                "created": "2024-10-06T05:10:16.614231+00:00",
                "descriptions": [],
                "discountAmount": 0,
                "discounts": [],
                "displayDescription": "description",
                "due": "2024-10-08T05:10:16.596933+00:00",
                "expiration": 5097600,
                "fee": 0,
                "fine": 2.0,
                "fineAmount": 0,
                "id": "10",
                "interest": 1.0,
                "interestAmount": 0,
                "link": "https://link.to/invoice",
                "name": "Sansa Stark",
                "nominalAmount": 431745,
                "pdf": "https://link.to/pdf.pdf",
                "rules": [],
                "splits": [],
                "status": "$logType",
                "tags": [
                  "tag1",
                  "tag2"
                ],
                "taxId": "123.456.789-10",
                "transactionIds": [],
                "updated": "2024-10-06T05:20:14.070559+00:00"
              },
              "type": "$logType"
            },
            "subscription": "$subscriptionType",
            "workspaceId": "1"
          }
        }
    """.trimIndent()

    val defaultInvoiceEvent = WebhookEventParser.parseEvent(defaultWebhookMessageBody()) as Event.InvoiceEvent
}