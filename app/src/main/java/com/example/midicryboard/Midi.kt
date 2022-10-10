package com.example.midicryboard

import android.util.Log
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import kotlin.experimental.or

object Midi {
    private val driver = MidiDriver.getInstance()
    var volume: Byte = 100

    fun start() {
        driver.start()
    }

    fun stop() {
        driver.stop()
    }

    private fun noteEvent(action: Byte, note: Byte, channel: Byte) {
        val event = ByteArray(3)
        event[0] = action or channel
        event[1] = note
        event[2] = volume
        driver.write(event)
    }

    private fun midiEvent(action: Byte, arg: Byte, channel: Byte) {
        val event = ByteArray(2)
        event[0] = action or channel
        event[1] = arg
        driver.write(event)
    }

    fun noteOn(note: Byte, channel: Byte) {
        noteEvent(MidiConstants.NOTE_ON, note, channel)
    }
    fun noteOff(note: Byte, channel: Byte) {
        noteEvent(MidiConstants.NOTE_OFF, note, channel)
    }

    fun changeProgram(channel: Byte, program: Byte) {
        midiEvent(MidiConstants.PROGRAM_CHANGE, program, channel)
        Log.println(Log.DEBUG, "ChangeProgram", "$channel $program")
    }
}