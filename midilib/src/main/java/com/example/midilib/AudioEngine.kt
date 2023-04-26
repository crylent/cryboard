package com.example.midilib

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

    external fun setInstrument(channel: Byte, instrument: Synthesizer)

    fun addEffect(channel: Byte, fx: SoundFX) = addEffectExternal(channel, fx).also {
        fx.linkToChannel(channel, it)
    }
    private external fun addEffectExternal(channel: Byte, fx: SoundFX): Byte
    external fun editEffect(channel: Byte, i: Byte, param: String, value: Float)
    external fun clearEffects(channel: Byte)
}