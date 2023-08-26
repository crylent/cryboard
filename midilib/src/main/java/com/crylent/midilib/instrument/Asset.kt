package com.crylent.midilib.instrument

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

sealed interface Asset {
    val note: Byte
    val isBaseAsset: Boolean

    fun getInputStream(context: Context): InputStream

    fun readBytes(context: Context): ByteArray {
        getInputStream(context).apply {
            val bytes = readBytes()
            close()
            return bytes
        }
    }
}

data class ApplicationAsset(
    override val note: Byte, val filename: String, override val isBaseAsset: Boolean
): Asset {
    override fun getInputStream(context: Context): InputStream = context.assets.open(filename)
}

data class AssetFromUri(
    override val note: Byte, val uri: Uri, override val isBaseAsset: Boolean
): Asset {
    override fun getInputStream(context: Context): InputStream = context.contentResolver.openInputStream(uri)!!
}

data class AssetFromFile(
    override val note: Byte, val file: File, override val isBaseAsset: Boolean
): Asset {
    override fun getInputStream(context: Context): InputStream = file.inputStream()
}

data class AssetFromBytes(
    override val note: Byte, val bytes: ByteArray, override val isBaseAsset: Boolean
): Asset {
    override fun getInputStream(context: Context) = bytes.inputStream()
    override fun readBytes(context: Context) = bytes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssetFromBytes) return false

        if (note != other.note) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (isBaseAsset != other.isBaseAsset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = note.toInt()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + isBaseAsset.hashCode()
        return result
    }
}