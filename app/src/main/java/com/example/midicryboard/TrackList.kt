package com.example.midicryboard

import com.example.midilib.instrument.Instrument

const val TRACKS_NUMBER = 8

object TrackList: ArrayList<TrackInfo>(TRACKS_NUMBER) {

    private lateinit var tracksRecyclerAdapter: TracksRecyclerAdapter

    fun linkRecyclerAdapter(adapter: TracksRecyclerAdapter) {
        tracksRecyclerAdapter = adapter
    }

    fun getTrackName(trackId: Byte) = this[trackId.toInt()].instrument.name
    //fun getInstrumentId(trackId: Byte) = this[trackId.toInt()].instrumentId

    val namesList
        get() = this.map { it.instrument.name }

    init {
        // Tracks by default
        /*addInstrument(MidiInstruments["Piano"]!!)
        addInstrument(MidiInstruments["Bright Piano"]!!)
        addInstrument(MidiInstruments["Electric Piano"]!!)
        addInstrument(MidiInstruments["Bright Electric"]!!)
        addInstrument(MidiInstruments["Clavinet"]!!)
        addInstrument(MidiInstruments["Organ"]!!)
        addInstrument(MidiInstruments["Electric Organ"]!!)*/
        //addInstrument(8)
    }

    private fun createTrackInfo(instrument: Instrument) = TrackInfo(
        instrument,
        TrackInfo.MAX_VOLUME
    )

    fun addInstrument(instrument: Instrument) {
        Midi.setInstrument(size.toByte(), instrument)
        add(createTrackInfo(instrument))
    }

    fun setTrackInfo(
        trackId: Byte,
        instrument: Instrument = this[trackId.toInt()].instrument,
        volume: Byte = this[trackId.toInt()].volume
    ) {
        setTrackInfo(
            trackId,
            TrackInfo(instrument, volume)
        )
    }

    fun setTrackInfo(trackId: Byte, trackInfo: TrackInfo) {
        Midi.setInstrument(trackId, trackInfo.instrument)
        this[trackId.toInt()] = trackInfo
        tracksRecyclerAdapter.updateItem(trackId, getTrackName(trackId))
    }

    fun openProject(project: CryboardProject) {
        project.tracks.forEachIndexed { index, trackInfo ->
            setTrackInfo(index.toByte(), trackInfo)
        }
    }
}