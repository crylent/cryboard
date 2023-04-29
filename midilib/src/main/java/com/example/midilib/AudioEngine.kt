package com.example.midilib

import com.example.midilib.instrument.Instrument
import com.example.midilib.soundfx.SoundFX

@Suppress("MemberVisibilityCanBePrivate")
object AudioEngine {
    init {
        System.loadLibrary("midilib")
    }

    const val AUTO_DEFINITION = -1
    const val MASTER: Byte = -1

    external fun start(sharedMode: Boolean = false, sampleRate: Int = AUTO_DEFINITION)
    external fun start()
    external fun stop()

    //external fun setAssetManager(mgr: AssetManager)

    external fun noteOn(channel: Byte, note: Byte, amplitude: Float)
    external fun noteOff(channel: Byte, note: Byte)
    external fun allNotesOff(channel: Byte)

    fun setInstrument(channel: Byte, instrument: Instrument) {
        instrument.apply {
            if (libIndex == null) {
                linkToLib(createInstrumentExternal(this))
            }
            setInstrumentExternal(channel, libIndex!!)
        }
    }
    private external fun createInstrumentExternal(instrument: Instrument): Int
    private external fun setInstrumentExternal(channel: Byte, instrument: Int)

    internal fun setInstrumentAttack(instrument: Int, attack: Float) {
        setInstrumentAttackExternal(instrument, attack)
    }
    private external fun setInstrumentAttackExternal(instrument: Int, attack: Float)

    internal fun setInstrumentDecay(instrument: Int, decay: Float) {
        setInstrumentDecayExternal(instrument, decay)
    }
    private external fun setInstrumentDecayExternal(instrument: Int, decay: Float)

    internal fun setInstrumentSustain(instrument: Int, sustain: Float) {
        setInstrumentSustainExternal(instrument, sustain)
    }
    private external fun setInstrumentSustainExternal(instrument: Int, sustain: Float)

    internal fun setInstrumentRelease(instrument: Int, release: Float) {
        setInstrumentReleaseExternal(instrument, release)
    }
    private external fun setInstrumentReleaseExternal(instrument: Int, release: Float)

    /*internal fun editInstrument(instrument: Int, param: String, value: Float) {
        editInstrumentExternal(instrument, param, value)
    }
    private external fun editInstrumentExternal(instrument: Int, param: String, value: Float)*/

    internal fun addOscillator(instrument: Int, oscillator: Oscillator) {
        addOscillatorExternal(instrument, oscillator)
    }
    private external fun addOscillatorExternal(instrument: Int, oscillator: Oscillator)

    /*internal fun editOscillator(instrument: Int, oscIndex: Int, param: String, value: Float) {
        editOscillatorExternal(instrument, oscIndex, param, value)
    }
    private external fun editOscillatorExternal(instrument: Int, oscIndex: Int, param: String, value: Float)*/

    internal fun setOscillatorShape(instrument: Int, oscIndex: Int, shape: Oscillator.Shape) {
        setOscillatorShapeExternal(instrument, oscIndex, shape.ordinal)
    }
    private external fun setOscillatorShapeExternal(instrument: Int, oscIndex: Int, shape: Int)

    internal fun setOscillatorAmplitude(instrument: Int, oscIndex: Int, amplitude: Float) {
        setOscillatorAmplitudeExternal(instrument, oscIndex, amplitude)
    }
    private external fun setOscillatorAmplitudeExternal(instrument: Int, oscIndex: Int, amplitude: Float)

    internal fun setOscillatorPhase(instrument: Int, oscIndex: Int, phase: Float) {
        setOscillatorPhaseExternal(instrument, oscIndex, phase)
    }
    private external fun setOscillatorPhaseExternal(instrument: Int, oscIndex: Int, phase: Float)

    internal fun setOscillatorFrequencyFactor(instrument: Int, oscIndex: Int, freqFactor: Float) {
        setOscillatorFrequencyFactorExternal(instrument, oscIndex, freqFactor)
    }
    private external fun setOscillatorFrequencyFactorExternal(instrument: Int, oscIndex: Int, freqFactor: Float)

    internal fun enableDetune(instrument: Int, oscIndex: Int, unisonVoices: Int, detune: Float) {
        enableDetuneExternal(instrument, oscIndex, unisonVoices, detune)
    }
    private external fun enableDetuneExternal(instrument: Int, oscIndex: Int, unisonVoices: Int, detune: Float)

    internal fun disableDetune(instrument: Int, oscIndex: Int) {
        disableDetuneExternal(instrument, oscIndex)
    }
    private external fun disableDetuneExternal(instrument: Int, oscIndex: Int)

    internal fun setUnisonVoices(instrument: Int, oscIndex: Int, unisonVoices: Int) {
        setUnisonVoicesExternal(instrument, oscIndex, unisonVoices)
    }
    private external fun setUnisonVoicesExternal(instrument: Int, oscIndex: Int, unisonVoices: Int)

    internal fun setDetuneLevel(instrument: Int, oscIndex: Int, detune: Float) {
        setDetuneLevelExternal(instrument, oscIndex, detune)
    }
    private external fun setDetuneLevelExternal(instrument: Int, oscIndex: Int, detune: Float)

    internal fun setPhaseShift(instrument: Int, oscIndex: Int, voice: Int, phase: Float) {
        setPhaseShiftExternal(instrument, oscIndex, voice, phase)
    }
    private external fun setPhaseShiftExternal(instrument: Int, oscIndex: Int, voice: Int, phase: Float)

    internal fun removeOscillator(instrument: Int, oscIndex: Int) {
        removeOscillatorExternal(instrument, oscIndex)
    }
    private external fun removeOscillatorExternal(instrument: Int, oscIndex: Int)

    fun addEffect(channel: Byte, fx: SoundFX) {
        fx.linkToChannel(
            channel,
            addEffectExternal(channel, fx)
        )
    }
    private external fun addEffectExternal(channel: Byte, fx: SoundFX): Byte

    internal fun editEffect(channel: Byte, i: Byte, param: String, value: Float) {
        editEffectExternal(channel, i, param, value)
    }
    private external fun editEffectExternal(channel: Byte, i: Byte, param: String, value: Float)

    external fun clearEffects(channel: Byte)
}