package com.example.midilib

data class Oscillator(
    var shape: Shape,
    var amplitude: Float,
    var phase: Float = 0f,
    var frequencyFactor: Float = 1f,
    var detune: Detune? = null
) {
    enum class Shape {
        SINE, TRIANGLE, SQUARE, SAW, REVERSE_SAW
    }

    class Detune(unisonVoices: Int, var detune: Float) {
        var unisonVoices = unisonVoices
            set(value) {
                _phases.addAll(List(value - field) { 0f })
                field = value
            }

        private val _phases = MutableList(unisonVoices) { 0f }
        val phases = _phases.toList()

        fun setPhaseShift(i: Int, phaseShift: Float) {
            _phases[i] = phaseShift;
        }

        fun getPhaseShift(i: Int) = _phases[i]
    }
}