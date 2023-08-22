package com.example.midicryboard.projectactivities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.midilib.instrument.Instrument

abstract class InstrumentTabFragment(protected var instrument: Instrument) : Fragment() {
    protected abstract val layout: Int
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layout, container, false).apply {
            createView()
        }.also { fragmentView = it }
    }

    protected abstract fun View.createView()
    protected abstract fun View.updateView()

    fun updateInstrument(newInstrument: Instrument) {
        instrument = newInstrument
        if (::fragmentView.isInitialized) fragmentView.updateView()
    }
}