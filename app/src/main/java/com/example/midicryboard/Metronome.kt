package com.example.midicryboard

import java.util.Timer
import kotlin.concurrent.timer

data class TimeSignature(val beats: Int = 4, val duration: Int = 4) {
    companion object {
        fun fromString(string: String): TimeSignature? {
            val regex = Regex("^[0-9]+/[0-9]+$")
            if (!string.matches(regex)) return null // is not a time signature
            val numbers = string.split("/").map { it.toInt() }
            return TimeSignature(numbers[0], numbers[1])
        }
    }

    override fun toString(): String {
        return "$beats/$duration"
    }
}

class Metronome(var tempo: Int = 120, var signature: TimeSignature = TimeSignature()) {

    private lateinit var onTimer: Timer
    private lateinit var offTimer: Timer

    private var volume: Byte = 127

    private var beats: Int = 0

    fun start() {
        if (running) stop() // prevent running several timers simultaneously
        val period = (MINUTE / tempo * 4 / signature.duration).toLong()
        onTimer = timer(period = period) {
            Midi.noteOn(if (beats == 0) METRONOME_DOWNBEAT else METRONOME_BEAT, METRONOME_CHANNEL, volume) // new measure
            beats++
            if (beats == signature.beats) beats = 0 // end of measure
        }
        offTimer = timer(period = period, initialDelay = BEAT_DURATION) {
            Midi.noteOff(METRONOME_BEAT, METRONOME_CHANNEL)
        }
        running = true
    }

    fun stop() {
        if (running) {
            onTimer.cancel()
            offTimer.cancel()
            running = false
        }
    }

    var running = false
        private set

    companion object {
        private const val MINUTE = 60 * 1000

        private const val BEAT_DURATION: Long = 100

        const val METRONOME_CHANNEL: Byte = 9
        private const val METRONOME_DOWNBEAT: Byte = 35
        private const val METRONOME_BEAT: Byte = 42
    }
}