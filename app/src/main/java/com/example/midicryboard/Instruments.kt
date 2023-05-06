package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.TrackList.createTrack
import com.example.midilib.instrument.Instrument
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
                Instrument.fromJson(context, it).apply {
                    if (it.optBoolean("default")) createTrack(this)
                    catList.add(this)
                }
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