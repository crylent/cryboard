package com.example.midilib.instrument

import com.example.midilib.AudioEngine

abstract class Instrument(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float
) {
    var attack = attack
        set(value) {
            field = value
            libIndex?.let { AudioEngine.setInstrumentAttack(it, value) }
            //updateParameter(ATTACK, value)
        }
    var decay = decay
        set(value) {
            field = value
            libIndex?.let { AudioEngine.setInstrumentDecay(it, value) }
            //updateParameter(DECAY, value)
        }
    var sustain = sustain
        set(value) {
            field = value
            libIndex?.let { AudioEngine.setInstrumentSustain(it, value) }
            //updateParameter(SUSTAIN, value)
        }
    var release = release
        set(value) {
            field = value
            libIndex?.let { AudioEngine.setInstrumentRelease(it, value) }
            //updateParameter(RELEASE, value)
        }

    internal var libIndex: Int? = null
        private set

    internal fun linkToLib(position: Int) {
        libIndex = position
    }

    /*private fun updateParameter(param: String, value: Float) {
        if (libIndex != null) {
            AudioEngine.editInstrument(libIndex!!, param, value)
        }
    }*/

    fun asAssetInstrument() = this as AssetInstrument
    fun asSynthInstrument() = this as SynthInstrument

    /*companion object {
        const val ATTACK = "attack"
        const val DECAY = "decay"
        const val SUSTAIN = "sustain"
        const val RELEASE = "release"
    }*/
}