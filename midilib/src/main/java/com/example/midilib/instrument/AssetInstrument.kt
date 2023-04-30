package com.example.midilib.instrument

import android.content.Context

@Suppress("unused")
open class AssetInstrument(
    attack: Float = 0f,
    decay: Float = 5f,
    sustain: Float = 0f,
    release: Float = 0f,
    repeatAssets: Boolean = true
): Instrument(attack, decay, sustain, release) {
    class Drums : AssetInstrument(0f, 0f, 1f, Float.POSITIVE_INFINITY, false)

    var repeatAssets = repeatAssets
        set(value) {
            field = value
            if (libIndex != NO_INDEX) setRepeatable(value)
        }

    fun loadAsset(context: Context, note: Byte, filename: String) {
        val stream = context.assets.open(filename)
        val bytes = stream.readBytes()
        externalLoadAsset(note, bytes, bytes.size)
        stream.close()
    }

    private external fun externalLoadAsset(note: Byte, wavData: ByteArray, dataSize: Int)
    private external fun setRepeatable(repeatable: Boolean)

    override fun clone() = AssetInstrument(attack, decay, sustain, release)
}