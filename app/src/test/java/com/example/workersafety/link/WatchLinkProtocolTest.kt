package com.example.workersafety.link

import org.junit.Assert.*
import org.junit.Test

class WatchLinkProtocolTest {
    private val id = "12345678-1234-1234-1234-123456789abc"
    private val other = "87654321-1234-1234-1234-123456789abc"

    @Test fun acceptedReceiptHasStableWireRepresentation() {
        val receipt = WatchLinkProtocol.Receipt(id, WatchLinkProtocol.Result.ACCEPTED)
        assertEquals("$id|ACCEPTED", WatchLinkProtocol.encode(receipt).toString(Charsets.UTF_8))
        assertEquals(receipt, WatchLinkProtocol.decode("$id|ACCEPTED".toByteArray()))
    }

    @Test fun allRejectionReasonsRoundTrip() {
        for (result in WatchLinkProtocol.Result.values()) {
            val receipt = WatchLinkProtocol.Receipt(id, result)
            assertEquals(receipt, WatchLinkProtocol.decode(WatchLinkProtocol.encode(receipt)))
        }
    }

    @Test fun malformedAndUnknownMessagesAreIgnored() {
        for (message in listOf("", "$id", "$id|UNKNOWN", "$id|ACCEPTED|extra",
            "bad|ACCEPTED", "1-1-1-1-1|ACCEPTED", "$id|accepted", "x".repeat(81))) {
            assertNull(message, WatchLinkProtocol.decode(message.toByteArray()))
        }
    }

    @Test fun lateReceiptCannotConfirmAnotherSend() {
        val receipt = WatchLinkProtocol.Receipt(id, WatchLinkProtocol.Result.ACCEPTED)
        assertFalse(WatchLinkProtocol.shouldApply(other, false, receipt))
        assertFalse(WatchLinkProtocol.shouldApply(null, false, receipt))
        assertTrue(WatchLinkProtocol.shouldApply(id, false, receipt))
    }

    @Test fun completedReceiptDoesNotRegressOnLogout() {
        val receipt = WatchLinkProtocol.Receipt(id, WatchLinkProtocol.Result.SIGN_IN_REQUIRED)
        assertFalse(WatchLinkProtocol.shouldApply(id, true, receipt))
    }

    @Test fun rejectedReceiptCanBeConfirmedAfterRetry() {
        val receipt = WatchLinkProtocol.Receipt(id, WatchLinkProtocol.Result.ACCEPTED)
        assertTrue(WatchLinkProtocol.shouldApply(id, false, receipt))
    }
}
