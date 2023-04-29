package com.example.midilib.instrument

import com.example.midilib.AudioEngine
import com.example.midilib.Oscillator

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
        if (oscillator.owner == null) {
            _oscillators.add(oscillator)
            oscillator.owner = this
        } else {
            TODO() // clone
        }
    }

    fun getOscillator(oscIndex: Int) = _oscillators[oscIndex]

    fun removeOscillator(oscillator: Oscillator) {
        _oscillators.remove(oscillator)
        if (libIndex != null) {
            AudioEngine.removeOscillator(libIndex!!, oscillator.oscIndex)
        }
    }

    fun removeOscillator(oscIndex: Int) {
        _oscillators.removeAt(oscIndex)
        if (libIndex != null) {
            AudioEngine.removeOscillator(libIndex!!, oscIndex)
        }
    }
}