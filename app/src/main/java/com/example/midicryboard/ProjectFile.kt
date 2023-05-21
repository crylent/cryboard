package com.example.midicryboard

import java.io.File

data class ProjectFile(val metadata: String, val midi: ByteArray): java.io.Serializable {
    constructor(metadata: String, midi: File): this(metadata, midi.readBytes())

    companion object {
        private const val serialVersionUID = -90000100L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectFile) return false

        if (metadata != other.metadata) return false
        if (!midi.contentEquals(other.midi)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = metadata.hashCode()
        result = 31 * result + midi.contentHashCode()
        return result
    }
}