package com.example.midilib

import com.example.midilib.instrument.Instrument
import com.example.midilib.soundfx.SoundFX

@Suppress("MemberVisibilityCanBePrivate")
object AudioEngine {
    init {
        System.loadLibrary("midilib")
    }

    const val AUTO_DEFINITION = -1
    const val MASTER: Byte = -1

    external fun start(sharedMode: Boolean = false, sampleRate: Int = AUTO_DEFINITION)
    external fun start()
    external fun stop()

    external fun noteOn(channel: Byte, note: Byte, amplitude: Float)
    external fun noteOff(channel: Byte, note: Byte)
    external fun allNotesOff(channel: Byte)

    fun setInstrument(channel: Byte, instrument: Instrument) {
        instrument.assignToChannel(channel)
    }

    fun addEffect(channel: Byte, fx: SoundFX) {
        fx.assignToChannel(channel)
    }

    external fun clearEffects(channel: Byte)
}