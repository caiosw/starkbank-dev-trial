package com.starkbank.devtrial.exceptions

class ErrorParsingJsonException(message: String) : StarkTrialException(message) {
    override fun getFailureReason(): StarkTrialExceptionFailureReason =
        StarkTrialExceptionFailureReason.ERROR_PARSING_JSON
}

class EventTypeNotFoundException(message: String) : StarkTrialException(message) {
    override fun getFailureReason(): StarkTrialExceptionFailureReason =
        StarkTrialExceptionFailureReason.EVENT_TYPE_NOT_FOUND
}

class EventTypeNotImplementedException(message: String) : StarkTrialException(message) {
    override fun getFailureReason(): StarkTrialExceptionFailureReason =
        StarkTrialExceptionFailureReason.EVENT_TYPE_NOT_IMPLEMENTED
}

class InvalidJsonFormatException(message: String) : StarkTrialException(message) {
    override fun getFailureReason(): StarkTrialExceptionFailureReason =
        StarkTrialExceptionFailureReason.INVALID_JSON_FORMAT
}