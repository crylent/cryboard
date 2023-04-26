package com.example.midicryboard

import android.util.Log
import java.util.*
import kotlin.concurrent.timerTask

data class TimeSignature(val num: Int = 4, val den: Int = 4) {
    companion object {
        fun fromString(string: String): TimeSignature? {
            val regex = Regex("^[0-9]+/[0-9]+$")
            if (!string.matches(regex)) return null // is not a time signature
            val numbers = string.split("/").map { it.toInt() }
            return TimeSignature(numbers[0], numbers[1])
        }
    }

    override fun toString(): String {
        return "$num/$den"
    }
}

object Metronome {
    private const val BEAT_DURATION: Long = 100

    const val METRONOME_CHANNEL: Byte = 9
    private const val METRONOME_DOWNBEAT: Byte = 35
    private const val METRONOME_BEAT: Byte = 42

    const val DEFAULT_TEMPO = 120

    var tempo: Int = DEFAULT_TEMPO
    var signature: TimeSignature = TimeSignature()

    var running = false
        private set

    private var volume: Byte = 127

    private var beats: Int = 0

    val period
        get() = (60e3 / tempo * 4 / signature.den).toLong()

    fun run(timer: Timer, startTime: Long) {
        beats = ((startTime % (period * signature.num) / period + 1) % signature.num).toInt() // first beat
        timer.schedule(timerTask {
            beat(timer)
        }, period - startTime % period, period)
        running = true
    }

    fun stop() {
        running = false
    }

    private fun beat(timer: Timer) {
        val beat = if (beats == 0) METRONOME_DOWNBEAT else METRONOME_BEAT
        Midi.noteOn(
            beat,
            METRONOME_CHANNEL,
            volume
        )
        beats = (beats + 1) % signature.num
        timer.schedule(timerTask {
            Midi.noteOff(beat, METRONOME_CHANNEL)
        }, BEAT_DURATION)
    }
}