package com.example.midilib

data class Synthesizer(
    val oscillators: MutableList<Oscillator>,
    var attack: Float,
    var decay: Float,
    var sustain: Float,
    var release: Float
)
