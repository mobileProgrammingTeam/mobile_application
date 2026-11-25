package com.example.workersafety.link

import java.util.UUID

/** Identical wire contract in the two independently built applications. */
object WatchLinkProtocol {
    const val SUMMARY_PATH = "/workersafety/summary"
    const val RECEIPT_PATH = "/workersafety/summary/receipt"
    const val SUMMARY_ID = "summary_id"
    const val WATCH_CAPABILITY = "workersafety_watch"
    const val PHONE_CAPABILITY = "workersafety_phone"

    enum class Result { ACCEPTED, SIGN_IN_REQUIRED, NEW_WINDOW_REQUIRED, INVALID_DATA, WATCH_MISMATCH }
    data class Receipt(val summaryId: String, val result: Result)

    fun validId(value: String?): Boolean = value != null && value.length == 36 &&
        runCatching { UUID.fromString(value).toString() == value }.getOrDefault(false)

    /** Only the current send may transition, and a completed receipt never regresses. */
    fun shouldApply(pendingId: String?, completed: Boolean, receipt: Receipt): Boolean =
        !completed && pendingId == receipt.summaryId && validId(receipt.summaryId)

    fun encode(receipt: Receipt): ByteArray {
        require(validId(receipt.summaryId))
        return "${receipt.summaryId}|${receipt.result.name}".toByteArray(Charsets.UTF_8)
    }

    fun decode(bytes: ByteArray): Receipt? {
        if (bytes.size !in 38..80) return null
        val fields = bytes.toString(Charsets.UTF_8).split('|')
        if (fields.size != 2 || !validId(fields[0])) return null
        val result = runCatching { Result.valueOf(fields[1]) }.getOrNull() ?: return null
        return Receipt(fields[0], result)
    }
}
