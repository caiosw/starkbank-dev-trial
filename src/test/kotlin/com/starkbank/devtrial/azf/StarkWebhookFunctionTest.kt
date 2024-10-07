package com.starkbank.devtrial.azf

import com.microsoft.azure.functions.*
import com.starkbank.utils.Parse
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import com.starkbank.devtrial.azf.StarkWebhookFunction

class StarkWebhookFunctionTest {
    val mockedParse = mockk<Parse>()

    val webhookMessageBody = """
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
                "status": "paid",
                "tags": [
                  "tag1",
                  "tag2"
                ],
                "taxId": "123.456.789-10",
                "transactionIds": [],
                "updated": "2024-10-06T05:20:14.070559+00:00"
              },
              "type": "paid"
            },
            "subscription": "invoice",
            "workspaceId": "1"
          }
        }
    """.trimIndent()

    @Test
    fun `(run) should receive a valid webhook from StarkBank as expected`() {
        val mockRequest = mockk<HttpRequestMessage<Optional<String>>>()
        val headers = mapOf(
            "digital-signature" to "validDigitalSignature",
            "some-other-header" to "someValue"
        )


        every { mockRequest.body } returns Optional.of(webhookMessageBody)
        every { mockRequest.httpMethod } returns HttpMethod.POST
        every { mockRequest.headers } returns headers

        // Mocking ExecutionContext
        val mockContext = mockk<ExecutionContext>(relaxed = true)

        // Mocking OutputBinding
        val mockOutput = mockk<OutputBinding<String>>(relaxed = true)
        val outputSlot = slot<String>()
        every { mockOutput.setValue(capture(outputSlot)) } answers {}


        StarkWebhookFunction.run(mockRequest, mockContext, mockOutput)

        verifySequence {
            mockRequest.body
            mockRequest.headers["digital-signature"]
        }

    }
}