package com.example.midicryboard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

private const val TRACKS = "tracks"
/*private const val TEMPO = "tempo"
private const val TIME_SIGNATURE = "timeSignature"
private const val NUM = "num"
private const val DEN = "den"

data class CryboardProject(val tracks: ArrayList<TrackInfo>, val tempo: Int, val timeSignature: TimeSignature) {
    constructor(): this(TrackList as ArrayList<TrackInfo>, Metronome.tempo, Metronome.signature)

    fun toJson() = JSONObject().apply {
        put(TRACKS, JSONArray().apply {
            tracks.forEach {
                put(it.toJson())
            }
        })
        put(TEMPO, tempo)
        put(TIME_SIGNATURE, JSONObject().apply {
            put(NUM, timeSignature.num)
            put(DEN, timeSignature.den)
        })
    }

    companion object {
        fun fromJson(context: Context, json: JSONObject): CryboardProject {
            val tracks = ArrayList<TrackInfo>().apply {
                json.getJSONArray(TRACKS).forEach {
                    add(TrackInfo.fromJson(context, it))
                }
            }
            val tempo = json.getInt(TEMPO)
            val timeSignature = json.getJSONObject(TIME_SIGNATURE)
            val num = timeSignature.getInt(NUM)
            val den = timeSignature.getInt(DEN)
            return CryboardProject(tracks, tempo, TimeSignature(num, den))
        }
    }
}*/

data class CryboardProject(val tracks: ArrayList<TrackInfo>) {
    constructor(): this(TrackList as ArrayList<TrackInfo>)

    fun toJson() = JSONObject().apply {
        put(TRACKS, JSONArray().apply {
            tracks.forEach {
                put(it.toJson())
            }
        })
    }

    companion object {
        fun fromJson(context: Context, json: JSONObject): CryboardProject {
            val tracks = ArrayList<TrackInfo>().apply {
                json.getJSONArray(TRACKS).forEach {
                    add(TrackInfo.fromJson(context, it))
                }
            }
            return CryboardProject(tracks)
        }
    }
}
