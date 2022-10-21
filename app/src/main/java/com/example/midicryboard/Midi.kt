package com.example.midicryboard

import android.util.Log
import com.leff.midi.MidiTrack
import com.leff.midi.event.ChannelEvent
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.experimental.or
import com.leff.midi.event.meta.TimeSignature as TimeSignatureEvent

object Midi {
    private val tracks = mutableListOf<MidiTrack>().apply {
        add(MidiTrack.createTempoTrack())
        repeat(TRACKS_NUMBER) {
            add(MidiTrack())
        }
    }

    private val driver = MidiDriver.getInstance()
    var volume: Byte = 100
        set(value) {
            if (value in 0..127) field = value
            else throw IllegalArgumentException()
        }

    private const val NOTE_OFF_VELOCITY: Byte = 0

    fun start() {
        driver.start()
    }
    fun stop() {
        driver.stop()
    }

    private val systemTime
        get() = System.currentTimeMillis()

    private var playing = false
    private var recording = false
    private var zeroTime: Long = 0

    fun startRecording() {
        recording = true
        startPlayback()
    }
    fun stopRecording() {
        recording = false
    }

    private lateinit var playbackTimer: Timer

    fun startPlayback() {
        playing = true
        playbackTimer = Timer()
        processEvents { _, event ->
            playbackTimer.schedule(timerTask {
                playMidiEvent(event)
            }, event.tick)
        }
        zeroTime = systemTime
    }
    fun stopPlayback() {
        playing = false
        stopRecording()
        if (this::playbackTimer.isInitialized) playbackTimer.cancel()
    }

    private fun playMidiEvent(event: MidiEvent) {
        event.apply {
            if (this is NoteOn) noteOn(noteValue.toByte(), channel.toByte(), velocity.toByte())
            else if (this is NoteOff) noteOff(noteValue.toByte(), channel.toByte())
        }
    }

    fun processEvents(lambda: (Int, MidiEvent) -> Unit) {
        tracks.subList(1, TRACKS_NUMBER).forEach { track ->
            val iterator = track.events.iterator()
            while (iterator.hasNext()) {
                val event = iterator.next()
                lambda(tracks.indexOf(track) - 1, event)
            }
        }
    }

    fun updateTempoAndSignature(metronome: Metronome) {
        tracks[0] = MidiTrack().apply {
            insertEvent(TimeSignatureEvent(
                0, 0,
                metronome.signature.beats, metronome.signature.duration,
                TimeSignatureEvent.DEFAULT_METER, TimeSignatureEvent.DEFAULT_DIVISION
            ))
            insertEvent(Tempo().apply { bpm = metronome.tempo.toFloat() })
        }
    }

    // records MIDI channel event (noteOn/noteOff) if recording is enabled
    private fun record(channel: Byte, channelEvent: ChannelEvent) {
        if (recording && channel != Metronome.METRONOME_CHANNEL) tracks[channel + 1].insertEvent(channelEvent)
    }

    private val currTime
        get() = systemTime - zeroTime

    private fun noteEvent(action: Byte, note: Byte, channel: Byte, velocity: Byte) {
        val event = ByteArray(3)
        event[0] = action or channel
        event[1] = note
        event[2] = velocity
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
    fun noteOn(note: Byte, channel: Byte, velocity: Byte) {
        noteEvent(MidiConstants.NOTE_ON, note, channel, velocity)
        record(channel, NoteOn(currTime, channel.toInt(), note.toInt(), velocity.toInt()))
    }
    fun noteOff(note: Byte, channel: Byte) {
        noteEvent(MidiConstants.NOTE_OFF, note, channel, NOTE_OFF_VELOCITY)
        record(channel, NoteOff(currTime, channel.toInt(), note.toInt(), NOTE_OFF_VELOCITY.toInt()))
    }

    fun changeProgram(channel: Byte, program: Byte) {
        midiEvent(MidiConstants.PROGRAM_CHANGE, program, channel)
        Log.println(Log.DEBUG, "ChangeProgram", "$channel $program")
    }

    fun clearTrack(channel: Byte) {
        tracks[channel + 1] = MidiTrack()
    }
}