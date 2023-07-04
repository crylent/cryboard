package com.example.midilib

import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Oscillator(
    shape: Shape,
    amplitude: Number = 1f,
    phase: Number = 0f,
    frequencyFactor: Number = 1f
): Cloneable {
    var shape = shape
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetShape(value.ordinal)
            }
        }

    var amplitude = amplitude.toFloat()
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetAmplitude(value)
            }
        }

    var phase = phase.toFloat()
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetPhase(value)
            }
        }

    var frequencyFactor = frequencyFactor.toFloat()
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                externalSetFreqFactor(value)
            }
        }

    var detune: Detune? = null
        private set

    internal var owner: SynthInstrument? = null

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
            _phases[voice] = phaseShift
            owner.apply {
                ifOwnerIsLinkedToLib {
                    externalSetPhaseShift(voice, phaseShift)
                }
            }
        }

        fun getPhaseShift(voice: Int) = _phases[voice]
    }

    fun enableDetune(unisonVoices: Int, detuneLevel: Number) {
        detune = Detune(this, unisonVoices, detuneLevel.toFloat())
        ifOwnerIsLinkedToLib {
            externalEnableDetune(unisonVoices, detuneLevel.toFloat())
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

    public override fun clone() = Oscillator(shape, amplitude, phase, frequencyFactor).also {
        if (detune != null) it.enableDetune(detune!!.unisonVoices, detune!!.detune)
    }
}