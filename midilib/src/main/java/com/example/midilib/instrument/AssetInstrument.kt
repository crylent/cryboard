package com.example.midilib.instrument

import android.content.Context

@Suppress("unused")
open class AssetInstrument(
    attack: Number = 0f,
    decay: Number = 5f,
    sustain: Number = 0f,
    release: Number = 0f,
    attackSharpness: Number = 1f,
    decaySharpness: Number = 1f,
    releaseSharpness: Number = 1f,
    var repeatAssets: Boolean = true
): Instrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness) {
    class Drums : AssetInstrument(0f, 0f, 1f, Float.POSITIVE_INFINITY,
        1f, 1f, 1f,false)

    init {
        addOnCreatedListener {
            externalSetRepeatable((it as AssetInstrument).repeatAssets)
        }
    }

    fun loadAsset(context: Context, note: Byte, filename: String, isBaseAsset: Boolean = false) {
        addOnCreatedListener {
            val stream = context.assets.open(filename)
            val bytes = stream.readBytes()
            externalLoadAsset(note, bytes, bytes.size, isBaseAsset)
            stream.close()
        }
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