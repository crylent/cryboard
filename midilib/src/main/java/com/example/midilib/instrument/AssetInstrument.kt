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
    repeatAssets: Boolean = true
): Instrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness) {
    class Drums : AssetInstrument(
        0f, 0f, 1f, Float.POSITIVE_INFINITY,
        1f, 1f, 1f,
        false
    )

    data class Asset(val note: Byte, val filename: String, val isBaseAsset: Boolean)

    private val assets = mutableListOf<Asset>()

    var repeatAssets = repeatAssets
        set(value) {
            field = value
            if (libIndex != NO_INDEX) externalSetRepeatable(value)
        }

    val assetsList get() = assets.toList()

    init {
        addOnCreatedListener {
            externalSetRepeatable((it as AssetInstrument).repeatAssets)
        }
    }

    fun loadAsset(context: Context, asset: Asset) {
        assets.add(asset)
        addOnCreatedListener {
            val stream = context.assets.open(asset.filename)
            val bytes = stream.readBytes()
            externalLoadAsset(asset.note, bytes, bytes.size, asset.isBaseAsset)
            stream.close()
        }
    }

    fun loadAsset(context: Context, note: Byte, filename: String, isBaseAsset: Boolean = false) {
        loadAsset(context, Asset(note, filename, isBaseAsset))
    }

    private external fun externalLoadAsset(note: Byte, wavData: ByteArray, dataSize: Int, isBaseAsset: Boolean)
    private external fun externalSetRepeatable(repeatable: Boolean)

    override fun clone() = AssetInstrument(
        attack, decay, sustain, release,
        attackSharpness, decaySharpness, releaseSharpness,
        repeatAssets
    )

    enum class ResamplingQuality {
        SINC_BEST_QUALITY,
        SINC_MEDIUM_QUALITY,
        SINC_FASTEST,
        ZERO_ORDER_HOLD,
        LINEAR,
    }

    companion object {
        fun setResamplingQuality(quality: ResamplingQuality) {
            externalSetResamplingQuality(quality.ordinal)
        }
        private external fun externalSetResamplingQuality(quality: Int)
    }
}