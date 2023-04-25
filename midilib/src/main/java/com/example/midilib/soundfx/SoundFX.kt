package com.example.midilib.soundfx

import com.example.midilib.MidiLib

abstract class SoundFX {
    abstract fun getId(): Int
    abstract fun getConfig(): Map<String, Float>

    fun getParameter(param: String): Float = getConfig()[param]!!

    private var fxChannel: Byte? = null
    private var fxPosition: Byte? = null

    internal fun linkToChannel(channel: Byte, position: Byte) {
        fxChannel = channel
        fxPosition = position
    }

    protected fun updateParameter(param: String, value: Float) {
        if (fxChannel != null && fxPosition != null) {
            MidiLib.editEffect(fxChannel!!, fxPosition!!, param, value)
        }
    }

    companion object {
        const val THRESHOLD = "threshold"
        const val LIMIT = "limit"
        const val ATTACK = "attack"
        const val RELEASE = "release"
    }
}