package com.crylent.midilib.instrument

@Suppress("unused")
abstract class Instrument(
    attack: Number,
    decay: Number,
    sustain: Number,
    release: Number,
    attackSharpness: Number,
    decaySharpness: Number,
    releaseSharpness: Number
): Cloneable {
    /** Optional - not used in library, but can be assigned for convenience **/
    var name = ""

    var attack = attack.toFloat()
        set(value) {
            field = value
            externalSetAttack(value)
        }

    var decay = decay.toFloat()
        set(value) {
            field = value
            externalSetDecay(value)
        }

    var sustain = sustain.toFloat()
        set(value) {
            field = value
            externalSetSustain(value)
        }

    var release = release.toFloat()
        set(value) {
            field = value
            externalSetRelease(value)
        }

    var attackSharpness = attackSharpness.toFloat()
        set(value) {
            field = value
            externalSetAttackSharpness(value)
        }

    var decaySharpness = decaySharpness.toFloat()
        set(value) {
            field = value
            externalSetDecaySharpness(value)
        }

    var releaseSharpness = releaseSharpness.toFloat()
        set(value) {
            field = value
            externalSetReleaseSharpness(value)
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
    private external fun externalSetAttackSharpness(value: Float)
    private external fun externalSetDecaySharpness(value: Float)
    private external fun externalSetReleaseSharpness(value: Float)

    fun asAssetInstrument() = this as AssetInstrument
    fun asSynthInstrument() = this as SynthInstrument

    public abstract override fun clone(): Instrument

    companion object {
        const val NO_INDEX = -1
    }
}