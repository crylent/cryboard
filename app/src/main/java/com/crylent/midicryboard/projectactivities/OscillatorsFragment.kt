package com.crylent.midicryboard.projectactivities

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crylent.midicryboard.R
import com.crylent.midicryboard.trackactivity.OscillatorsRecyclerAdapter
import com.crylent.midilib.instrument.Instrument
import com.crylent.midilib.instrument.SynthInstrument

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