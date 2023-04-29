package com.example.midilib

import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument

class Oscillator(
    shape: Shape,
    amplitude: Float = 1f,
    phase: Float = 0f,
    frequencyFactor: Float = 1f
) {
    var shape = shape
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetShape(value.ordinal)
            }
        }

    var amplitude = amplitude
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetAmplitude(value)
            }
        }

    var phase = phase
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetPhase(value)
            }
        }

    var frequencyFactor = frequencyFactor
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetFreqFactor(value)
            }
        }

    var detune: Detune? = null
        private set

    internal var owner: SynthInstrument? = null
    val oscIndex
        get() = owner?.oscillators!!.indexOf(this)

    enum class Shape {
        SINE, TRIANGLE, SQUARE, SAW, REVERSE_SAW
    }

    class Detune(val owner: Oscillator, unisonVoices: Int, detune: Float) {
        var unisonVoices = unisonVoices
            set(value) {
                _phases.addAll(List(value - field) { 0f })
                field = value
                owner.apply {
                    ifOwnerIsLinkedToLib {
                        externalSetUnisonVoices(value)
                    }
                }
            }

        var detune = detune
            set(value) {
                field = value
                owner.apply {
                    ifOwnerIsLinkedToLib {
                        externalSetDetuneLevel(value)
                    }
                }
            }

        private val _phases = MutableList(unisonVoices) { 0f }
        val phases = _phases.toList()

        fun setPhaseShift(voice: Int, phaseShift: Float) {
            _phases[voice] = phaseShift;
            owner.apply {
                ifOwnerIsLinkedToLib {
                    externalSetPhaseShift(voice, phaseShift)
                }
            }
        }

        fun getPhaseShift(voice: Int) = _phases[voice]
    }

    fun enableDetune(unisonVoices: Int, detuneLevel: Float) {
        detune = Detune(this, unisonVoices, detuneLevel)
        ifOwnerIsLinkedToLib {
            externalEnableDetune(unisonVoices, detuneLevel)
        }
    }
    fun disableDetune() {
        detune = null
        ifOwnerIsLinkedToLib {
            externalDisableDetune()
        }
    }

    private fun ifOwnerIsLinkedToLib(lambda: (libIndex: Int)->Unit) {
        if (owner != null && owner!!.libIndex != Instrument.NO_INDEX) lambda(owner!!.libIndex)
    }

    private external fun externalSetShape(shape: Int)
    private external fun externalSetAmplitude(value: Float)
    private external fun externalSetPhase(value: Float)
    private external fun externalSetFreqFactor(value: Float)
    private external fun externalEnableDetune(unisonVoices: Int, detuneLevel: Float)
    private external fun externalDisableDetune()
    private external fun externalSetUnisonVoices(value: Int)
    private external fun externalSetDetuneLevel(value: Float)
    private external fun externalSetPhaseShift(voice: Int, value: Float)
}