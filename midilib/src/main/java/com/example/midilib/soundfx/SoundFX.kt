package com.example.midilib.soundfx

import com.example.midilib.AudioEngine

abstract class SoundFX: Cloneable {
    abstract fun getId(): Int
    abstract fun getConfig(): Map<String, Float>

    fun getParameter(param: String): Float = getConfig()[param]!!

    private var linkedChannel: Byte? = null
    private var fxIndex: Byte? = null

    internal fun linkToChannel(channel: Byte, position: Byte) {
        linkedChannel = channel
        fxIndex = position
    }

    protected fun updateParameter(param: String, value: Float) {
        if (linkedChannel != null && fxIndex != null) {
            AudioEngine.editEffect(linkedChannel!!, fxIndex!!, param, value)
        }
    }

    companion object {
        const val THRESHOLD = "threshold"
        const val LIMIT = "limit"
        const val ATTACK = "attack"
        const val RELEASE = "release"
    }
}