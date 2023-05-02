package com.example.midicryboard

import com.example.midilib.instrument.Instrument

data class TrackInfo(var instrument: Instrument, var volume: Byte): java.io.Serializable {
    companion object {
        const val MAX_VOLUME: Byte = 127
    }
}