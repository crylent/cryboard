package com.example.midicryboard

import com.example.midicryboard.mainactivity.TracksRecyclerAdapter
import com.example.midilib.instrument.Instrument

object TrackList: ArrayList<TrackInfo>() {

    private lateinit var tracksRecyclerAdapter: TracksRecyclerAdapter

    fun linkRecyclerAdapter(adapter: TracksRecyclerAdapter) {
        tracksRecyclerAdapter = adapter
    }

    val namesList
        get() = this.map { it.instrument.name }

    private fun createTrackInfo(instrument: Instrument) = TrackInfo(
        instrument,
        TrackInfo.MAX_VOLUME
    )

    fun createTrack(instrument: Instrument) {
        createTrack(createTrackInfo(instrument))
    }

    fun createTrack(trackInfo: TrackInfo) {
        Midi.setInstrument(size.toByte(), trackInfo.instrument)
        add(trackInfo)
        listeners.forEach { it.onTrackCreated() }
    }

    private interface TracksListener {
        fun onTrackCreated()
        fun onTracksCleared()
    }

    private val listeners = mutableListOf<TracksListener>()

    fun linkCollection(collection: MutableCollection<out Any>, onTracksClearedAction: (() -> Unit)? = null, onTrackCreatedAction: () -> Unit) {
        listeners.add(object : TracksListener {
            override fun onTrackCreated() {
                onTrackCreatedAction()
            }

            override fun onTracksCleared() {
                collection.clear()
                onTracksClearedAction?.invoke()
            }
        })
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

    private fun setTrackInfo(trackId: Byte, trackInfo: TrackInfo) {
        Midi.setInstrument(trackId, trackInfo.instrument)
        this[trackId.toInt()] = trackInfo
        tracksRecyclerAdapter.updateItem(trackId)
    }

    fun readTracksFromProject(project: CryboardProject) {
        clear()
        listeners.forEach { it.onTracksCleared() }
        project.tracks.forEach {
            createTrack(it)
        }
        tracksRecyclerAdapter.resetItems()
    }
}