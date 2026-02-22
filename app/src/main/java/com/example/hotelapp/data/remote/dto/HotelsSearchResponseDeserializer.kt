package com.example.hotelapp.data.remote.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Prihvata odgovor kao objekat {"hotels": [...]} ili {"data": [...]} ili direktan niz [...].
 * Za objekat ne koristimo context.deserialize na ceo odgovor da ne bi izazvali rekurziju.
 */
class HotelsSearchResponseDeserializer : JsonDeserializer<HotelsSearchResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): HotelsSearchResponse {
        return when {
            json.isJsonArray -> {
                val list = context.deserialize<List<HotelDto>>(json, listType)
                HotelsSearchResponse(data = list)
            }
            json.isJsonObject -> {
                val obj = json.asJsonObject
                val hotels = obj.get("hotels")?.takeIf { it.isJsonArray }
                    ?.let { context.deserialize<List<HotelDto>>(it, listType) }
                val data = obj.get("data")?.takeIf { it.isJsonArray }
                    ?.let { context.deserialize<List<HotelDto>>(it, listType) }
                HotelsSearchResponse(hotels = hotels, data = data)
            }
            else -> throw JsonParseException("Očekivan JSON niz ili objekat")
        }
    }

    private companion object {
        private val listType = object : TypeToken<List<HotelDto>>() {}.type
    }
}
