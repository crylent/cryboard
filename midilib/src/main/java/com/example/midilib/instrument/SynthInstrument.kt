package com.example.midilib.instrument

import com.example.midilib.Oscillator

@Suppress("unused")
class SynthInstrument(
    attack: Float = 0f,
    decay: Float = 5f,
    sustain: Float = 0f,
    release: Float = 0f,
    oscillators: List<Oscillator> = listOf()
): Instrument(attack, decay, sustain, release) {
    private val _oscillators = oscillators.toMutableList()
    val oscillators
        get() = _oscillators.toList()

    fun addOscillator(oscillator: Oscillator) {
        val osc = if (oscillator.owner == null) oscillator else oscillator.clone()
        _oscillators.add(osc)
        osc.owner = this
        if (libIndex != NO_INDEX) externalAddOscillator(oscillator)
    }

    fun getOscillator(oscIndex: Int) = _oscillators[oscIndex]

    fun removeOscillator(oscillator: Oscillator) {
        _oscillators.remove(oscillator)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscillator.oscIndex)
    }

    fun removeOscillator(oscIndex: Int) {
        _oscillators.removeAt(oscIndex)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscIndex)
    }

    private external fun externalAddOscillator(oscillator: Oscillator)
    private external fun externalRemoveOscillator(index: Int)

    override fun clone() = SynthInstrument(attack, decay, sustain, release).also {
        _oscillators.forEach { osc ->
            it.addOscillator(osc)
        }
    }
}