package com.example.midilib.instrument

class AssetInstrument(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float
): Instrument(attack, decay, sustain, release) {
    fun loadAsset() {

    }

    override fun clone() = AssetInstrument(attack, decay, sustain, release)
}