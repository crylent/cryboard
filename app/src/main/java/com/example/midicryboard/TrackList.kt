package com.example.midicryboard

import org.billthefarmer.mididriver.GeneralMidiConstants

const val TRACKS_NUMBER = 8

object TrackList: ArrayList<TrackInfo>(TRACKS_NUMBER) {

    private lateinit var tracksRecyclerAdapter: TracksRecyclerAdapter

    fun linkRecyclerAdapter(adapter: TracksRecyclerAdapter) {
        tracksRecyclerAdapter = adapter
    }

    fun getTrackName(trackId: Byte) = this[trackId.toInt()].instrument.name
    fun getInstrumentId(trackId: Byte) = this[trackId.toInt()].instrumentId

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
        instrumentId,
        TrackInfo.MAX_VOLUME
    )

    private fun addInstrument(id: Byte) {
        Midi.changeProgram(size.toByte(), id)
        add(createTrackInfo(id))
    }

    fun setTrackInfo(
        trackId: Byte,
        instrumentId: Byte = this[trackId.toInt()].instrumentId,
        volume: Byte = this[trackId.toInt()].volume
    ) {
        setTrackInfo(
            trackId,
            TrackInfo(instrumentId, volume)
        )
    }

    fun setTrackInfo(trackId: Byte, trackInfo: TrackInfo) {
        Midi.changeProgram(trackId, trackInfo.instrumentId)
        this[trackId.toInt()] = trackInfo
        tracksRecyclerAdapter.updateItem(trackId, getTrackName(trackId))
    }

    fun openProject(project: CryboardProject) {
        project.tracks.forEachIndexed { index, trackInfo ->
            setTrackInfo(index.toByte(), trackInfo)
        }
    }
}