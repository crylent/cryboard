package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.TrackList.createTrack
import com.example.midilib.instrument.Instrument
import org.json.JSONObject
import java.nio.charset.Charset

class Instruments private constructor(context: Context): ArrayList<Instruments.Category>() {
    data class Category(
        val name: String,
        val items: MutableList<Instrument>,
        val hidden: Boolean = false
    ) {
        operator fun get(i: Int) = items[i]
        operator fun get(i: Byte) = items[i.toInt()]
    }

    init {
        JSONObject(
            context.resources.openRawResource(R.raw.instruments).readBytes().toString(Charset.defaultCharset())
        ).getJSONArray("categories").forEach { category ->
            val items = mutableListOf<Instrument>()
            category.getJSONArray("instruments").forEach {
                Instrument.fromJson(context, it).apply {
                    if (it.optBoolean("default")) createTrack(this)
                    items.add(this)
                }
            }
            val hidden = category.optBoolean("hidden")
            add(Category(category.getString("name"), items, hidden))
        }
    }

    private val all get() = flatMap { it.items }
    val byNames get() = buildMap {
        all.forEach { put(it.name, it) }
    }

    val categories = filter { !it.hidden }.map { it.name }

    companion object {
        lateinit var instance: Instruments
            private set

        fun init(context: Context): Instruments {
            instance = Instruments(context)
            return instance
        }

        operator fun get(i: Int) = instance[i]
        operator fun get(i: Byte) = instance[i.toInt()]
        operator fun get(name: String) = instance.byNames[name]

        fun getCategoryIndex(instrument: Instrument): Int? {
            instance.forEachIndexed { index, category ->
                if (category.items.contains(instrument)) return index
            }
            return null
        }

        val categories get() = instance.categories
    }
}