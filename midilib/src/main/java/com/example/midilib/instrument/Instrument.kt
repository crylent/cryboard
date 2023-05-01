package com.example.midilib.instrument

@Suppress("unused")
abstract class Instrument(
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float
): Cloneable {
    var attack = attack
        set(value) {
            field = value
            externalSetAttack(value)
        }

    var decay = decay
        set(value) {
            field = value
            externalSetDecay(value)
        }

    var sustain = sustain
        set(value) {
            field = value
            externalSetSustain(value)
        }

    var release = release
        set(value) {
            field = value
            externalSetRelease(value)
        }

    internal var libIndex: Int = NO_INDEX
        private set

    fun assignToChannel(channel: Byte) {
        if (libIndex == NO_INDEX) {
            libIndex = externalCreate()
            onCreatedListeners.forEach {
                it.onCreated(this)
            }
            onCreatedListeners.clear()
        }
        externalAssignToChannel(channel)
    }

    private val onCreatedListeners = ArrayList<OnCreatedListener>()

    fun interface OnCreatedListener {
        fun onCreated(instrument: Instrument)
    }

    fun addOnCreatedListener(listener: OnCreatedListener) {
        if (libIndex == NO_INDEX) onCreatedListeners.add(listener)
        else listener.onCreated(this) // already created
    }

    private external fun externalCreate(): Int
    private external fun externalAssignToChannel(channel: Byte)

    private external fun externalSetAttack(value: Float)
    private external fun externalSetDecay(value: Float)
    private external fun externalSetSustain(value: Float)
    private external fun externalSetRelease(value: Float)

    fun asAssetInstrument() = this as AssetInstrument
    fun asSynthInstrument() = this as SynthInstrument

    public abstract override fun clone(): Instrument

    companion object {
        const val NO_INDEX = -1
    }
}