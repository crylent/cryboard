package com.example.midicryboard

import java.io.Serializable

data class CryboardProject(val tracks: ArrayList<TrackInfo>): Serializable {
    constructor(): this(TrackList as ArrayList<TrackInfo>)

    companion object {
        private const val serialVersionUID: Long = -90000101L
    }
}

