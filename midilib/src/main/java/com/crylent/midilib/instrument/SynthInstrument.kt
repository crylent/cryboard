package com.crylent.midilib.instrument

import com.crylent.midilib.Oscillator

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

    fun addDefaultOscillator(): Int {
        return addOscillator(Oscillator(Oscillator.Shape.SINE))
    }

    fun addOscillator(oscillator: Oscillator): Int {
        val osc = if (oscillator.owner == null) oscillator else oscillator.clone()
        osc.owner = this
        oscillators.add(osc)
        if (libIndex != NO_INDEX) externalAddOscillator(oscillator)
        return oscCount - 1
    }

    fun getOscillator(oscIndex: Int) = oscillators[oscIndex]

    internal fun getOscillatorIndex(oscillator: Oscillator) = oscillators.indexOf(oscillator)

    val oscCount get() = oscillators.size

    fun removeOscillator(oscillator: Oscillator) {
        oscillators.remove(oscillator)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscillators.indexOf(oscillator))
    }

    fun removeOscillator(oscIndex: Int) {
        oscillators.removeAt(oscIndex)
        if (libIndex != NO_INDEX) externalRemoveOscillator(oscIndex)
    }

    fun removeLastOscillator(): Int {
        val index = oscCount - 1
        removeOscillator(index)
        return index
    }

    fun enableOscillator(oscIndex: Int) {
        oscillators[oscIndex].enabled = true
        if (libIndex != NO_INDEX) externalEnableOscillator(oscIndex)
    }

    internal fun enableOscillator(oscillator: Oscillator) {
        if (libIndex != NO_INDEX) externalEnableOscillator(oscillators.indexOf(oscillator))
    }

    fun disableOscillator(oscIndex: Int) {
        oscillators[oscIndex].enabled = false
        if (libIndex != NO_INDEX) externalDisableOscillator(oscIndex)
    }

    internal fun disableOscillator(oscillator: Oscillator) {
        if (libIndex != NO_INDEX) externalDisableOscillator(oscillators.indexOf(oscillator))
    }

    fun forEachOscillator(function: (Oscillator) -> Unit) {
        oscillators.forEach {
            function(it)
        }
    }

    private external fun externalAddOscillator(oscillator: Oscillator)
    private external fun externalRemoveOscillator(index: Int)
    private external fun externalEnableOscillator(index: Int)
    private external fun externalDisableOscillator(index: Int)

    override fun clone() = SynthInstrument(
        attack, decay, sustain, release,
        attackSharpness, decaySharpness, releaseSharpness,
        oscillators
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SynthInstrument) return false

        if (oscillators != other.oscillators) return false

        return true
    }

    override fun hashCode(): Int {
        return oscillators.hashCode()
    }
}