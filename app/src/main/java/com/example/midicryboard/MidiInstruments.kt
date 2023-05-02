package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.TrackList.addInstrument
import com.example.midilib.instrument.AssetInstrument
import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class MidiInstruments private constructor(context: Context): HashMap<String, List<Instrument>>() {
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
                val instrument = if (it.getBoolean("synth"))
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
                if (it.getBoolean("default")) addInstrument(instrument)
                catList.add(instrument)
            }
            this[category.getString("name")] = catList.toList()
        }
    }

    private val categories = map { it.key }

    private val all = buildMap<String, Instrument> {
        forEach { put(it.value.name, it.value) }
    }

    fun byName(name: String) = all[name]

    private fun getCategoryName(instrument: Instrument): String {
        forEach {
            if (it.value.contains(instrument)) return it.key
        }
        return ""
    }

    fun getCategoryId(instrument: Instrument) = categories.indexOf(getCategoryName(instrument))

    companion object {
        lateinit var instance: MidiInstruments
            private set

        fun init(context: Context): MidiInstruments {
            instance = MidiInstruments(context)
            return instance
        }

        val categories get() = instance.categories
        val all get() = instance.all
        operator fun get(s: String): Instrument? = all[s]

        fun byName(name: String) = instance.byName(name)

        //fun getCategoryName(instrument: Instrument) = instance.getCategoryName(instrument)
        fun getCategoryId(instrument: Instrument) = instance.getCategoryId(instrument)
    }
}

private fun JSONArray.forEach(function: (JSONObject) -> Unit) {
    for (i in 0 until length()) {
        function(getJSONObject(i))
    }
}

private fun JSONObject.getDoubleOrDefault(name: String, default: Number) =
    if (has(name)) getDouble(name) else default.toDouble()