package com.crylent.midicryboard

import android.content.Context
import com.crylent.midicryboard.TrackList.createTrack
import com.crylent.midicryboard.projectactivities.presetsDir
import com.crylent.midilib.instrument.Instrument
import org.json.JSONObject
import java.nio.charset.Charset

class Instruments private constructor(context: Context): LinkedHashMap<String, Instruments.Category>() {
    data class Category(
        val items: MutableList<Instrument>,
        val hidden: Boolean = false
    ) {
        operator fun get(i: Int) = items[i]
        operator fun get(i: Byte) = items[i.toInt()]

        val name get() = categoryNames[getCategoryIndex(this)!!]
    }

    init {
        // Built-in presets
        JSONObject(
            context.resources.openRawResource(R.raw.instruments).readBytes().toString(Charset.defaultCharset())
        ).getJSONArray("categories").forEach { category ->
            val items = mutableListOf<Instrument>()
            category.optJSONArray("instruments")?.forEach {
                Instrument.fromJson(context, it).apply {
                    if (it.optBoolean("default")) createTrack(this)
                    items.add(this)
                }
            }
            val hidden = category.optBoolean("hidden")
            put(category.getString("name"), Category(items, hidden))
        }

        // Custom presets
        for (category in context.presetsDir.listFiles()!!.filter { !it.isFile }) {
            category.listFiles()?.filter { it.name.endsWith(".json", true) }?.forEach {
                get(category.nameWithoutExtension)!!.items.add(
                    Instrument.fromJson(
                        context,
                        JSONObject(
                            it.readBytes().toString(Charset.defaultCharset())
                        )
                    )
                )
            }
        }
    }

    private val all get() = values.flatMap { it.items }
    val instrumentsByNames get() = buildMap {
        all.forEach { put(it.name, it) }
    }

    val categoryNames = filter { !it.value.hidden }.map { it.key }

    companion object {
        lateinit var instance: Instruments
            private set

        fun init(context: Context): Instruments {
            instance = Instruments(context)
            return instance
        }

        private val categories get() = instance.values.toList()

        fun getCategoryById(i: Int) = categories[i]
        fun findInstrument(name: String) = instance.instrumentsByNames[name]

        fun getCategoryIndex(category: Category): Int? {
            categories.forEachIndexed { index, categoryWithIndex ->
                if (category == categoryWithIndex) return index
            }
            return null
        }

        fun getCategoryIndex(instrument: Instrument): Int? {
            categories.forEachIndexed { index, category ->
                if (category.items.contains(instrument)) return index
            }
            return null
        }

        val categoryNames get() = instance.categoryNames
    }
}