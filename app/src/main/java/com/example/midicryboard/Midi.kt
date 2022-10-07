package com.example.midicryboard

import org.billthefarmer.mididriver.MidiDriver

object Midi {
    private val driver = MidiDriver.getInstance()
    var volume: Byte = 127

    fun start() {
        driver.start()
    }

    fun stop() {
        driver.stop()
    }

    private fun noteEvent(note: Int, action: Int, channel: Int) {
        val event = ByteArray(3)
        event[0] = (action or channel).toByte()
        event[1] = note.toByte()
        event[2] = volume
        driver.write(event)
    }

    fun noteOn(note: Int, channel: Int) {
        noteEvent(note, 0x90, channel)
    }
    fun noteOff(note: Int, channel: Int) {
        noteEvent(note, 0x80, channel)
    }
}