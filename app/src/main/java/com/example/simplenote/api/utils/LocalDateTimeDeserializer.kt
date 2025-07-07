package com.example.simplenote.api.utils

import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * A Gson deserializer that will parse strings like
 *   "2025-07-07 21:31:44.003364+00"
 * (i.e. yyyy-MM-dd HH:mm:ss.SSSSSSX)
 * into a LocalDateTime (dropping the offset).
 */
class LocalDateTimeDeserializer(pattern: String) : JsonDeserializer<LocalDateTime> {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(pattern)

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime {
        // First parse into OffsetDateTime to consume the "+00"
        val odt = OffsetDateTime.parse(json.asString, formatter)
        // Then drop the offset
        return odt.toLocalDateTime()
    }
}
