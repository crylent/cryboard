package com.example.midilib

data class Oscillator(
    var shape: Shape,
    var amplitude: Float,
    var phase: Float,
    var frequencyFactor: Float,
    var unisonVoices: Int,
    var detune: Float
) {
    enum class Shape {
        SINE, TRIANGLE, SQUARE, SAW, REVERSE_SAW
    }
}