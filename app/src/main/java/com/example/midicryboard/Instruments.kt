package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.TrackList.addInstrument
import com.example.midilib.instrument.AssetInstrument
import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class Instruments private constructor(context: Context): ArrayList<Instruments.Category>() {
    data class Category(val name: String, val items: List<Instrument>) {
        operator fun get(i: Int) = items[i]
        operator fun get(i: Byte) = items[i.toInt()]
    }

    init {
        JSONObject(
            context.resources.openRawResource(R.raw.instruments).readBytes().toString(Charset.defaultCharset())
        ).getJSONArray("categories").forEach { category ->
            val catList = mutableListOf<Instrument>()
            category.getJSONArray("instruments").forEach {
                val attack = it.getDoubleOrDefault("attack", 0)
                val attackSharpness = it.getDoubleOrDefault( "attackSharpness", 1)
                val decay = it.getDoubleOrDefault("decay", 5)
                val decaySharpness = it.getDoubleOrDefault("decaySharpness", 1)
                val sustain = it.getDoubleOrDefault("sustain", 0)
                val release = it.getDoubleOrDefault("release", 0)
                val releaseSharpness = it.getDoubleOrDefault("releaseSharpness", 1)
                val instrument = if (it.getBooleanOrFalse("synth"))
                    SynthInstrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness).apply {
                        TODO("oscillators")
                    }
                else AssetInstrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness).apply {
                    loadAsset(
                        context,
                        it.getInt("base_note").toByte(),
                        "wav/${it.getJSONArray("samples").getString(0)}.wav",
                        true
                    )
                    repeatAssets = true
                }
                instrument.name = it.getString("name")
                if (it.getBooleanOrFalse("default")) addInstrument(instrument)
                catList.add(instrument)
            }
            add(Category(category.getString("name"), catList.toList()))
        }
    }

    val categories = map { it.name }

    companion object {
        lateinit var instance: Instruments
            private set

        fun init(context: Context): Instruments {
            instance = Instruments(context)
            return instance
        }

        operator fun get(i: Int) = instance[i]
        operator fun get(i: Byte) = instance[i.toInt()]

        fun getCategoryIndex(instrument: Instrument): Int? {
            instance.forEachIndexed { index, category ->
                if (category.items.contains(instrument)) return index
            }
            return null
        }

        val categories get() = instance.categories
    }
}

private fun JSONArray.forEach(function: (JSONObject) -> Unit) {
    for (i in 0 until length()) {
        function(getJSONObject(i))
    }
}

private fun JSONObject.getDoubleOrDefault(name: String, default: Number) =
    if (has(name)) getDouble(name) else default.toDouble()

private fun JSONObject.getBooleanOrFalse(name: String) =
    if (has(name)) getBoolean(name) else false