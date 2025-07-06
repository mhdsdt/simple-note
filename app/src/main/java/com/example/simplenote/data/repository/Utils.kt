package com.example.simplenote.data.repository

import org.json.JSONObject

fun parseDrfErrorBody(baseMessage: String, errorBody: String?): String {
    if (errorBody == null) {
        return "$baseMessage No error body"
    }

    val json = JSONObject(errorBody)

    // If DRF returned a top-level "detail", use it:
    if (json.has("detail")) {
        return json.getString("detail")
    }

    // Otherwise look for "errors" array and pull out each "detail"
    if (json.has("errors")) {
        val arr = json.getJSONArray("errors")
        val messages = mutableListOf<String>()
        messages += baseMessage
        for (i in 0 until arr.length()) {
            val errObj = arr.getJSONObject(i)
            // Here we grab errObj["detail"] (or .optString to be safe)
            val msg = errObj.optString("detail", null.toString())
            messages += msg
        }
        // Join the messages with a prefix of "\n-" for each message
        val errorMessage = if (messages.isNotEmpty()) {
            messages.joinToString("\n- ")
        } else {
            "$baseMessage Unknown error (no details provided)"
        }
        return errorMessage
    }

    return "$baseMessage Unknown error"
}
