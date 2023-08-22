package com.example.midicryboard.projectactivities

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.R
import com.example.midicryboard.trackactivity.OscillatorsRecyclerAdapter
import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument

class OscillatorsFragment(instrument: Instrument) : InstrumentTabFragment(instrument) {
    override val layout: Int = R.layout.fragment_oscillators
    lateinit var oscillatorsView: RecyclerView

    override fun View.createView() {
        oscillatorsView = findViewById<RecyclerView>(R.id.oscillators).apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        updateView()
    }

    override fun View.updateView() {
        oscillatorsView.adapter = if (instrument is SynthInstrument)
            OscillatorsRecyclerAdapter(instrument as SynthInstrument)
        else null
    }
}