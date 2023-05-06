package com.example.midilib.instrument

import com.example.midilib.Oscillator

@Suppress("unused", "MemberVisibilityCanBePrivate")
class SynthInstrument(
    attack: Number = 0f,
    decay: Number = 5f,
    sustain: Number = 0f,
    release: Number = 0f,
    attackSharpness: Number = 1f,
    decaySharpness: Number = 1f,
    releaseSharpness: Number = 1f,
    private val oscillators: MutableList<Oscillator> = mutableListOf()
): Instrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness) {

    fun addOscillator(oscillator: Oscillator) {
        val osc = if (oscillator.owner == null) oscillator else oscillator.clone()
        oscillators.add(osc)
        osc.owner = this
        if (libIndex != NO_INDEX) externalAddOscillator(oscillator)
    }

    fun getOscillator(oscIndex: Int) = oscillators[oscIndex]

    fun removeOscillator(oscillator: Oscillator) {
        oscillators.remove(oscillator)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscillators.indexOf(oscillator))
    }

    fun removeOscillator(oscIndex: Int) {
        oscillators.removeAt(oscIndex)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscIndex)
    }

    private external fun externalAddOscillator(oscillator: Oscillator)
    private external fun externalRemoveOscillator(index: Int)

    override fun clone() = SynthInstrument(
        attack, decay, sustain, release,
        attackSharpness, decaySharpness, releaseSharpness,
        oscillators
    )
}