package com.crylent.midicryboard.projectactivities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crylent.midilib.instrument.AssetInstrument
import com.crylent.midilib.instrument.Instrument
import com.crylent.midilib.instrument.SynthInstrument

private const val NUM_TABS_SYNTH = 3
private const val NUM_TABS_ASSET = 2

class InstrumentTabsAdapter(private var instrument: Instrument, fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = if (instrument is SynthInstrument) NUM_TABS_SYNTH else NUM_TABS_ASSET

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EnvelopeFragment(instrument)
            1 -> EffectsFragment(instrument)
            2 -> OscillatorsFragment(instrument)
            else -> throw IllegalArgumentException()
        }.also { fragments.add(it) }
    }

    private val fragments = arrayListOf<InstrumentTabFragment>()


    fun updateFragments(newInstrument: Instrument) {
        val prevInstrumentWasSynth = instrument is SynthInstrument
        instrument = newInstrument
        if (instrument is SynthInstrument && !prevInstrumentWasSynth) { // need 3 tabs layout
            notifyItemInserted(OSCILLATORS_TAB)
        }
        else if (instrument is AssetInstrument && prevInstrumentWasSynth) { // need 2 tabs layout
            notifyItemRemoved(OSCILLATORS_TAB)
        }
        fragments.forEach {
            it.updateInstrument(newInstrument)
        }
    }

    companion object {
        private const val OSCILLATORS_TAB = 2
    }
}