package com.starkbank.devtrial.utils

import com.starkbank.devtrial.Constants.LAST_SCHEDULED_DATE_TIME
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZoneId
import java.time.ZoneOffset

class TimerTest {
    @Test
    fun `(shouldRunCronJob) should return true when the current date time UTC is the last time that we should send invoices`() {
        val testDateTime = LAST_SCHEDULED_DATE_TIME.toInstant(ZoneOffset.UTC)

        val fixedClock = Clock.fixed(testDateTime, ZoneId.of("UTC"))


        assert(Timer.shouldRunCronJob(fixedClock))
    }

    @Test
    fun `(shouldRunCronJob) should return true when the current date time UTC is before`() {
        val testDateTime = LAST_SCHEDULED_DATE_TIME.minusHours(6).toInstant(ZoneOffset.UTC)

        val fixedClock = Clock.fixed(testDateTime, ZoneId.of("UTC"))

        assert(Timer.shouldRunCronJob(fixedClock))
    }

    @Test
    fun `(shouldRunCronJob) should return false when the current date time UTC is after the last time we should send invoices`() {
        val testDateTime = LAST_SCHEDULED_DATE_TIME.plusHours(2).toInstant(ZoneOffset.UTC)

        val fixedClock = Clock.fixed(testDateTime, ZoneId.of("UTC"))

        assert(!Timer.shouldRunCronJob(fixedClock))
    }


}