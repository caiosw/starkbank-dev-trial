package com.starkbank.devtrial.exceptions

abstract class StarkTrialException(message: String) : Exception(message) {
    abstract fun getFailureReason(): StarkTrialExceptionFailureReason
}