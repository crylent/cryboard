package com.example.midilib

object MidiLib {
    init {
        System.loadLibrary("midilib")
    }

    const val DEFAULT_SAMPLE_RATE = 48000

    external fun start(sharedMode: Boolean = false, sampleRate: Int = 48000)
    external fun start()
    external fun stop()

    external fun noteOn(channel: Byte, note: Byte, amplitude: Float)
    external fun noteOff(channel: Byte, note: Byte)
}