package com.starkbank.devtrial

import com.starkbank.devtrial.DefaultEntities.defaultWebhookMessageBody
import com.starkbank.devtrial.exceptions.InvalidJsonFormatException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WebhookEventParserTest {
    @Test
    fun `(parseEvent) should parse a valid event webhook as expected`() {
        val actual = WebhookEventParser.parseEvent(defaultWebhookMessageBody()).toString()

        val expected = """
            InvoiceEvent({
              "log": {
                "created": "2024-10-06T05:20:14.070484+00:00",
                "type": "paid",
                "errors": [],
                "invoice": {
                  "amount": 431745,
                  "due": "2024-10-08T05:10:16.596933+00:00",
                  "taxId": "123.456.789-10",
                  "name": "Sansa Stark",
                  "expiration": 5097600,
                  "fine": 2.0,
                  "interest": 1.0,
                  "descriptions": [],
                  "discounts": [],
                  "rules": [],
                  "splits": [],
                  "tags": [
                    "tag1",
                    "tag2"
                  ],
                  "pdf": "https://link.to/pdf.pdf",
                  "link": "https://link.to/invoice",
                  "nominalAmount": 431745,
                  "fineAmount": 0,
                  "interestAmount": 0,
                  "discountAmount": 0,
                  "brcode": "brcode",
                  "fee": 0,
                  "transactionIds": [],
                  "status": "paid",
                  "created": "2024-10-06T05:10:16.614231+00:00",
                  "updated": "2024-10-06T05:20:14.070559+00:00",
                  "id": "10"
                },
                "id": "10"
              },
              "created": "2024-10-06T05:20:20.331385+00:00",
              "subscription": "invoice",
              "workspaceId": "1",
              "id": "10"
            })
        """.trimIndent()

        assertEquals(expected, actual)
    }

    @Test
    fun `(parseEvent) should throw error if the 'event' property was not found in the json`() {
        val input = """{"key":"value"}"""

        assertThrows<InvalidJsonFormatException> {
            WebhookEventParser.parseEvent(input)
        }
    }
}