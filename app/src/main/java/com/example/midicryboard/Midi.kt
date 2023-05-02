package com.example.midicryboard

import com.example.midilib.AudioEngine
import com.example.midilib.instrument.Instrument
import com.example.midilib.soundfx.Limiter
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.ChannelEvent
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask
import com.leff.midi.event.meta.TimeSignature as TimeSignatureEvent

object Midi {
    private val tracks = mutableListOf<MidiTrack>().apply {
        add(MidiTrack.createTempoTrack())
        repeat(TRACKS_NUMBER) {
            add(MidiTrack())
        }
    }

    var volume: Byte = 100
        set(value) {
            if (value in 0..127) field = value
            else throw IllegalArgumentException()
        }

    private const val NOTE_OFF_VELOCITY: Byte = 0

    fun start() {
        AudioEngine.apply {
            start()
            //addEffect(MASTER, Limiter())
        }
    }
    fun stop() {
        AudioEngine.stop()
    }

    private val systemTime
        get() = System.currentTimeMillis()
    private var systemTimeOnStart = 0L
    var staticPointerTime = 0L
    var movingPointerInitTime = 0L

    val time
        get() = movingPointerInitTime + if (playing) (systemTime - systemTimeOnStart) else 0
    private val ticks get() = timeToTicks(time)

    var playing = false
        private set
    private var recording = false

    private val oneTick
        get() = Metronome.DEFAULT_TEMPO.toDouble() / Metronome.tempo

    private fun timeToTicks(t: Long) = (t / oneTick).toLong()
    fun ticksToTime(ticks: Long) = (ticks * oneTick).toLong()

    val lengthInTicks get() = tracks.maxOf { it.lengthInTicks }
    val lengthInMillis get() = ticksToTime(lengthInTicks)

    fun startRecording() {
        recording = true
        startPlayback()
    }
    fun stopRecording() {
        recording = false
    }

    private lateinit var playbackTimer: Timer

    fun startPlayback() {
        playbackTimer = Timer()
        processEvents(ALL_TRACKS) { _, event ->
            val eventTime = ticksToTime(event.tick) - time
            if (eventTime >= 0) playbackTimer.schedule(timerTask {
                playMidiEvent(event)
            }, eventTime)
        }
        Metronome.run(playbackTimer, time)
        systemTimeOnStart = systemTime
        playing = true
    }
    fun pausePlayback() {
        allNotesOff()
        movingPointerInitTime = time
        playing = false
        if (this::playbackTimer.isInitialized) playbackTimer.cancel()
        Metronome.stop()
    }
    fun stopPlayback() {
        pausePlayback()
        stopRecording()
        movingPointerInitTime = staticPointerTime
    }

    private fun playMidiEvent(event: MidiEvent) {
        event.apply {
            if (this is NoteOn) noteOn(noteValue.toByte(), channel.toByte(), velocity.toByte())
            else if (this is NoteOff) noteOff(noteValue.toByte(), channel.toByte())
        }
    }

    const val ALL_TRACKS: Byte = -1

    fun processEvents(trackId: Byte, lambda: (Byte, MidiEvent) -> Unit) {
        if (trackId == ALL_TRACKS) {
            for (currTrackId in 0 until TRACKS_NUMBER) processEventsOnTrack(currTrackId.toByte(), lambda)
        }
        else processEventsOnTrack(trackId, lambda)
    }

    private fun processEventsOnTrack(trackId: Byte, lambda: (Byte, MidiEvent) -> Unit) {
        tracks[trackId + 1].events.apply {
            synchronized(this) {
                forEach { lambda(trackId, it) }
            }
        }
    }

    fun writeTempoAndSignature() {
        tracks[0] = MidiTrack().apply {
            insertEvent(TimeSignatureEvent(
                0, 0,
                Metronome.signature.num, Metronome.signature.den,
                TimeSignatureEvent.DEFAULT_METER, TimeSignatureEvent.DEFAULT_DIVISION
            ))
            insertEvent(Tempo().apply {
                bpm = Metronome.tempo.toFloat()
            })
        }
    }

    private fun readTempoAndSignature() {
        var tempoDetected = false
        var signatureDetected = false
        tracks[0].events.forEach {
            if (!tempoDetected && it is Tempo) {
                Metronome.tempo = it.bpm.toInt()
                tempoDetected = true
            }
            else if (!signatureDetected && it is TimeSignatureEvent) {
                Metronome.signature = TimeSignature(it.numerator, it.realDenominator)
                signatureDetected = true
            }
        }
    }

    // records MIDI channel event (noteOn/noteOff) if recording is enabled
    private fun recordIfEnabled(channel: Byte, channelEvent: ChannelEvent) {
        if (recording && channel != Metronome.METRONOME_CHANNEL) tracks[channel + 1].apply {
            synchronized(events) {
                tracks[channel + 1].insertEvent(channelEvent)
            }
        }
    }

    private val notesOn = Array(TRACKS_NUMBER) { mutableSetOf<Byte>() }

    fun noteOn(note: Byte, channel: Byte) {
        noteOn(note, channel, volume)
    }
    fun noteOn(note: Byte, channel: Byte, velocity: Byte) {
        AudioEngine.noteOn(channel, note, velocity / 127.0f)
        if (channel != Metronome.METRONOME_CHANNEL) {
            notesOn[channel.toInt()].add(note)
            recordIfEnabled(
                channel,
                NoteOn(ticks, channel.toInt(), note.toInt(), velocity.toInt())
            )
        }
    }
    fun noteOff(note: Byte, channel: Byte) {
        AudioEngine.noteOff(channel, note)
        if (channel != Metronome.METRONOME_CHANNEL) {
            notesOn[channel.toInt()].remove(note)
            recordIfEnabled(
                channel,
                NoteOff(ticks, channel.toInt(), note.toInt(), NOTE_OFF_VELOCITY.toInt())
            )
        }
    }
    fun allNotesOff(channel: Byte) { // all notes off on one channel
        AudioEngine.allNotesOff(channel)
        if (channel != Metronome.METRONOME_CHANNEL) {
            notesOn[channel.toInt()].toSet().forEach {
                recordIfEnabled(
                    channel,
                    NoteOff(ticks, channel.toInt(), it.toInt(), NOTE_OFF_VELOCITY.toInt())
                )
            }
        }
        notesOn[channel.toInt()].clear()
    }
    fun allNotesOff() { // all notes off on all channels
        for (channel in 0 until TRACKS_NUMBER)
            allNotesOff(channel.toByte())
    }

    fun setInstrument(channel: Byte, instrument: Instrument) {
        instrument.assignToChannel(channel)
    }

    fun clearTrack(channel: Byte) {
        tracks[channel + 1] = MidiTrack()
    }

    fun writeToFile(file: File) {
        MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks).writeToFile(file)
    }

    fun readFromFile(file: File) {
        MidiFile(file).tracks.forEachIndexed { index, midiTrack ->
            tracks[index] = midiTrack
        }
        readTempoAndSignature()
    }
}