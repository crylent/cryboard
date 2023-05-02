package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.TrackList.addInstrument
import com.example.midilib.instrument.AssetInstrument
import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument
import org.json.JSONObject
import java.nio.charset.Charset

class MidiInstruments private constructor(context: Context): HashMap<String, List<Instrument>>() {
    //data class Instrument(val name: String, val id: Byte)

    //private val res = MainActivity.resources
    //private val categories = mutableListOf<String>()

    init {
        val jsonCategories = JSONObject(
            context.resources.openRawResource(R.raw.instruments).readBytes().toString(Charset.defaultCharset())
        ).getJSONArray("categories")
        for (c in 0 until jsonCategories.length()) {
            val category = jsonCategories.getJSONObject(c)
            this[category.getString("name")] = mutableListOf<Instrument>().apply {
                val jsonInstruments = category.getJSONArray("instruments")
                for (i in 0 until jsonInstruments.length()) {
                    val instrument = jsonInstruments.getJSONObject(i)
                    val attack = instrument.getDouble("attack")
                    val decay = instrument.getDouble("decay")
                    val sustain = instrument.getDouble("sustain")
                    val release = instrument.getDouble("release")
                    add((if (instrument.getBoolean("synth"))
                        SynthInstrument(attack, decay, sustain, release).apply {
                            TODO("oscillators")
                        }
                        else AssetInstrument(attack, decay, sustain, release).apply {
                            loadAsset(
                                context,
                                instrument.getInt("base_note").toByte(),
                                "wav/${instrument.getJSONArray("samples").getString(0)}.wav",
                                true
                            )
                            repeatAssets = true
                        })
                        .also {
                            it.name = instrument.getString("name")
                            if (instrument.getBoolean("default")) {
                                addInstrument(it)
                            }
                        }
                    )
                }
            }.toList()
        }
    }

    private val categories = map { it.key }

    private val all = buildMap<String, Instrument> {
        forEach { put(it.value.name, it.value) }
    }

    /*fun categoryById(id: Byte): ArrayList<Instrument> = instruments.values.elementAt(id.toInt())

    private fun findSomethingByInstrumentId(id: Byte, returnLambda: (instrument: Instrument, category: ArrayList<Instrument>) -> Any): Any {
        if (id < 0) throw IllegalArgumentException()
        instruments.values.forEach { category ->
            category.forEach { instrument ->
                if (instrument.id == id) return returnLambda(instrument, category)
            }
        }
        throw RuntimeException()
    }

    fun findInstrumentById(id: Byte): Instrument {
        return findSomethingByInstrumentId(id) { instrument, _ -> instrument } as Instrument
    }

    fun getCategoryId(instrumentId: Byte): Byte {
        return findSomethingByInstrumentId(instrumentId) { _, category -> instruments.values.indexOf(category).toByte() } as Byte
    }*/

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