package com.example.simplenote.data.repository

import org.json.JSONObject

fun parseDrfErrorBody(errorBody: String): String {
    val json = JSONObject(errorBody)

    // If DRF returned a top-level "detail", use it:
    if (json.has("detail")) {
        return json.getString("detail")
    }

    // Otherwise look for "errors" array and pull out each "detail"
    if (json.has("errors")) {
        val arr = json.getJSONArray("errors")
        val messages = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            val errObj = arr.getJSONObject(i)
            // Here we grab errObj["detail"] (or .optString to be safe)
            val msg = errObj.optString("detail", null)
            if (msg != null) {
                messages += msg
            }
        }
        // You can join them, or just take the first one
        return messages.firstOrNull()
            ?: "Unknown error (no details provided)"
    }

    return "Unknown error"
}
