package com.example.midicryboard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

private const val TRACKS = "tracks"

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
