package com.crylent.midilib.instrument

import android.content.Context
import android.net.Uri
import java.io.File

@Suppress("unused", "MemberVisibilityCanBePrivate")
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
    class Single : AssetInstrument(
        0f, 0f, 1f, Float.POSITIVE_INFINITY,
        repeatAssets = false
    )

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
            val bytes = asset.readBytes(context)
            externalLoadAsset(asset.note, bytes, bytes.size, asset.isBaseAsset)
        }
    }

    /**
     * Load asset from assets folder of application
     */
    fun loadAsset(context: Context, note: Byte, filename: String, isBaseAsset: Boolean = false) {
        loadAsset(context, ApplicationAsset(note, filename, isBaseAsset))
    }

    /**
     * Load asset from URI using content resolver
     */
    fun loadAsset(context: Context, note: Byte, uri: Uri, isBaseAsset: Boolean = false) {
        loadAsset(context, AssetFromUri(note, uri, isBaseAsset))
    }

    /**
     * Load asset from file
     */
    fun loadAsset(context: Context, note: Byte, file: File, isBaseAsset: Boolean = false) {
        loadAsset(context, AssetFromFile(note, file, isBaseAsset))
    }

    /**
     * Load asset from byte array
     */
    fun loadAsset(context: Context, note: Byte, bytes: ByteArray, isBaseAsset: Boolean = false) {
        loadAsset(context, AssetFromBytes(note, bytes, isBaseAsset))
    }

    private external fun externalLoadAsset(note: Byte, wavData: ByteArray, dataSize: Int, isBaseAsset: Boolean)
    private external fun externalSetRepeatable(repeatable: Boolean)

    override fun clone() = AssetInstrument(
        attack, decay, sustain, release,
        attackSharpness, decaySharpness, releaseSharpness,
        repeatAssets
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssetInstrument) return false

        if (assets != other.assets) return false
        if (repeatAssets != other.repeatAssets) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assets.hashCode()
        result = 31 * result + repeatAssets.hashCode()
        return result
    }

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