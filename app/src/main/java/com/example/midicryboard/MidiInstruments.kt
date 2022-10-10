package com.example.midicryboard

import com.example.midicryboard.activity.MainActivity

object MidiInstruments {
    data class Instrument(val name: String, val id: Byte)

    private val res = MainActivity.resources
    private val categories = res.getStringArray(R.array.Categories)

    private val instruments: MutableMap<String, ArrayList<Instrument>> = mutableMapOf()

    init {
        val tree = res.obtainTypedArray(R.array.Instruments)
        var id = 0
        for (i in 0 until tree.length()) {
            val instrArray = arrayListOf<Instrument>()
            res.getStringArray(tree.getResourceId(i, 0)).forEach {
                instrArray.add(Instrument(it, id.toByte()))
                id += 1
            }
            instruments[categories[i]] = instrArray
        }
        tree.recycle()
    }

    fun categoryById(id: Byte): ArrayList<Instrument> = instruments.values.elementAt(id.toInt())

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
    }
}