package com.example.midilib

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
                AudioEngine.setOscillatorShape(it, oscIndex, value)
            }
        }
    var amplitude = amplitude
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                AudioEngine.setOscillatorAmplitude(it, oscIndex, value)
            }
            //updateParameter(AMPLITUDE, value)
        }
    var phase = phase
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                AudioEngine.setOscillatorPhase(it, oscIndex, value)
            }
            //updateParameter(PHASE, value)
        }
    var frequencyFactor = frequencyFactor
        set(value) {
            field = value
            ifOwnerIsLinkedToLib {
                AudioEngine.setOscillatorFrequencyFactor(it, oscIndex, value)
            }
            //updateParameter(FREQ_FACTOR, value)
        }
    var detune: Detune? = null
        private set

    internal var owner: SynthInstrument? = null
    internal val oscIndex
        get() = owner?.oscillators!!.indexOf(this)

    /*private fun updateParameter(param: String, value: Float) {
        ifOwnerIsLinkedToLib {
            AudioEngine.editOscillator(it, oscIndex, param, value)
        }
    }*/

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
                        AudioEngine.setUnisonVoices(it, oscIndex, value)
                    }
                }
            }

        var detune = detune
            set(value) {
                field = value
                owner.apply {
                    ifOwnerIsLinkedToLib {
                        AudioEngine.setDetuneLevel(it, oscIndex, value)
                    }
                }
            }

        private val _phases = MutableList(unisonVoices) { 0f }
        val phases = _phases.toList()

        fun setPhaseShift(voice: Int, phaseShift: Float) {
            _phases[voice] = phaseShift;
            owner.apply {
                ifOwnerIsLinkedToLib {
                    AudioEngine.setPhaseShift(it, oscIndex, voice, phaseShift)
                }
            }
        }

        fun getPhaseShift(voice: Int) = _phases[voice]
    }

    fun enableDetune(unisonVoices: Int, detuneLevel: Float) {
        detune = Detune(this, unisonVoices, detuneLevel)
        ifOwnerIsLinkedToLib {
            AudioEngine.enableDetune(it, oscIndex, unisonVoices, detuneLevel)
        }
    }
    fun disableDetune() {
        detune = null
        ifOwnerIsLinkedToLib {
            AudioEngine.disableDetune(it, oscIndex)
        }
    }

    private fun ifOwnerIsLinkedToLib(lambda: (libIndex: Int)->Unit) {
        if (owner != null && owner!!.libIndex != null) lambda(owner!!.libIndex!!)
    }

    /*companion object {
        const val AMPLITUDE = "amplitude"
        const val PHASE = "phase"
        const val FREQ_FACTOR = "frequencyFactor"
    }*/
}