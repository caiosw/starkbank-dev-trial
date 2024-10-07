package com.starkbank.devtrial

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.starkbank.*
import com.starkbank.devtrial.exceptions.InvalidJsonFormatException
import com.starkbank.utils.GsonEvent


object WebhookEventParser {
    fun parseEvent(eventMessage: String): Event {
        val gson = GsonEvent.getInstance()

        val jsonObject = (Gson().fromJson(eventMessage, JsonObject::class.java) as JsonObject)



        if (!jsonObject.has("event")) {
            throw InvalidJsonFormatException("Property 'event' not found in json message: $eventMessage")
        }

        val eventJsonObject = jsonObject["event"].asJsonObject
        val event = gson.fromJson(eventJsonObject, Event::class.java) as Event

        return event
    }
}