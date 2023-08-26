package com.crylent.midicryboard

import android.content.Context
import com.crylent.midilib.instrument.Instrument
import org.json.JSONObject

private const val INSTRUMENT = "instrument"
private const val VOLUME = "volume"

data class TrackInfo(var instrument: Instrument, var volume: Byte) {
    companion object {
        const val MAX_VOLUME: Byte = 127

        fun fromJson(context: Context, json: JSONObject) = TrackInfo(
            Instrument.fromJson(context, json.getJSONObject(INSTRUMENT)),
            json.getInt(VOLUME).toByte()
        )
    }

    fun toJson(context: Context) = JSONObject().apply {
        put(INSTRUMENT, instrument.toJson(context))
        put(VOLUME, volume)
    }
}