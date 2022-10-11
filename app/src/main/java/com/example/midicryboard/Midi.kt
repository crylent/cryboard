package com.example.midicryboard

import android.util.Log
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import kotlin.experimental.or

object Midi {
    private val driver = MidiDriver.getInstance()
    var volume: Byte = 100
        set(value) {
            if (value in 0..100) field = value
            else throw IllegalArgumentException()
        }

    private const val NOTE_OFF_VELOCITY: Byte = 0

    fun start() {
        driver.start()
    }

    fun stop() {
        driver.stop()
    }

    private fun noteEvent(action: Byte, note: Byte, channel: Byte, volume: Byte) {
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
        noteOn(note, channel, volume)
    }
    fun noteOn(note: Byte, channel: Byte, volume: Byte) {
        noteEvent(MidiConstants.NOTE_ON, note, channel, volume)
    }
    fun noteOff(note: Byte, channel: Byte) {
        noteEvent(MidiConstants.NOTE_OFF, note, channel, NOTE_OFF_VELOCITY)
    }

    fun changeProgram(channel: Byte, program: Byte) {
        midiEvent(MidiConstants.PROGRAM_CHANGE, program, channel)
        Log.println(Log.DEBUG, "ChangeProgram", "$channel $program")
    }
}