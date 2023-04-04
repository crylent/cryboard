package com.example.midicryboard

data class TrackInfo(var instrumentId: Byte, var volume: Byte): java.io.Serializable {
    val instrument
        get() = MidiInstruments.findInstrumentById(instrumentId)

    companion object {
        const val MAX_VOLUME: Byte = 127
    }
}