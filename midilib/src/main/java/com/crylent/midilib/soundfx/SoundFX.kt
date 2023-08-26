package com.crylent.midilib.soundfx

abstract class SoundFX: Cloneable {
    abstract fun getId(): Int
    abstract fun getConfig(): Map<String, Float>

    fun getParameter(param: String): Float = getConfig()[param]!!

    private var linkedChannel: Byte = NOT_LINKED
    private var fxIndex: Byte = NOT_LINKED

    internal fun assignToChannel(channel: Byte) {
        linkedChannel = channel
        fxIndex = externalAssignToChannel(channel)
    }

    protected fun updateParameter(param: String, value: Float) {
        if (linkedChannel != NOT_LINKED) {
            externalEditEffect(param, value)
        }
    }

    private external fun externalAssignToChannel(channel: Byte): Byte
    private external fun externalEditEffect(param: String, value: Float)

    companion object {
        const val NOT_LINKED: Byte = -1

        const val THRESHOLD = "threshold"
        const val LIMIT = "limit"
        const val ATTACK = "attack"
        const val RELEASE = "release"
    }

    public abstract override fun clone(): SoundFX
}