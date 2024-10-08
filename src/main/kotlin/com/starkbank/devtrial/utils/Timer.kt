package com.starkbank.devtrial.utils

import com.starkbank.devtrial.Constants.LAST_SCHEDULED_DATE_TIME
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object Timer {
    fun shouldRunCronJob(clock: Clock): Boolean {
        val upperBoundDateTime = LAST_SCHEDULED_DATE_TIME.plusHours(1)

        val now = LocalDateTime.now(clock)

        return now.isBefore(upperBoundDateTime)
    }
}