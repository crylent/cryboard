package com.example.midicryboard

import org.billthefarmer.mididriver.GeneralMidiConstants

const val TRACKS_NUMBER = 8

class TrackList: ArrayList<TrackInfo>(TRACKS_NUMBER) {
    fun getTrackName(trackId: Byte) = this[trackId.toInt()].instrument.name
    fun getInstrumentId(trackId: Byte) = this[trackId.toInt()].instrument.id

    val namesList
        get() = this.map { it.instrument.name }

    init {
        // Tracks by default
        addInstrument(GeneralMidiConstants.ACOUSTIC_GRAND_PIANO)
        addInstrument(GeneralMidiConstants.VIBRAPHONE)
        addInstrument(GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN)
        addInstrument(GeneralMidiConstants.ELECTRIC_BASS_FINGER)
        addInstrument(GeneralMidiConstants.VIOLIN)
        addInstrument(GeneralMidiConstants.TRUMPET)
        addInstrument(GeneralMidiConstants.FLUTE)
        addInstrument(GeneralMidiConstants.PAD_3_CHOIR)
    }

    private fun createTrackInfo(instrumentId: Byte) = TrackInfo(
        MidiInstruments.findInstrumentById(instrumentId)
    )

    private fun addInstrument(id: Byte) {
        Midi.changeProgram(size.toByte(), id)
        add(createTrackInfo(id))
    }

    fun setTrackInfo(trackId: Byte, instrumentId: Byte) {
        Midi.changeProgram(trackId, instrumentId)
        this[trackId.toInt()] = createTrackInfo(instrumentId)
    }
}