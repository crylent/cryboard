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
            if (libIndex != NO_INDEX) externalSetRepeatable(value)
        }

    fun loadAsset(context: Context, note: Byte, filename: String, isBaseAsset: Boolean = false) {
        val stream = context.assets.open(filename)
        val bytes = stream.readBytes()
        externalLoadAsset(note, bytes, bytes.size, isBaseAsset)
        stream.close()
    }

    private external fun externalLoadAsset(note: Byte, wavData: ByteArray, dataSize: Int, isBaseAsset: Boolean)
    private external fun externalSetRepeatable(repeatable: Boolean)

    override fun clone() = AssetInstrument(attack, decay, sustain, release)

    enum class ResamplingQuality {
        SINC_BEST_QUALITY,
        SINC_MEDIUM_QUALITY,
        SINC_FASTEST,
        ZERO_ORDER_HOLD,
        LINEAR,
    }

    companion object {
        fun setResamplingQuality(quality: ResamplingQuality) {
            externalSetResamplingQuality(quality.ordinal);
        }
        private external fun externalSetResamplingQuality(quality: Int)
    }
}