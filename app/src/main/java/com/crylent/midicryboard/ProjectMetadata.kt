package com.crylent.midicryboard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

private const val TRACKS = "tracks"

data class ProjectMetadata(val tracks: ArrayList<TrackInfo>) {
    constructor(): this(TrackList as ArrayList<TrackInfo>)

    fun toJson(context: Context) = JSONObject().apply {
        put(TRACKS, JSONArray().apply {
            tracks.forEach {
                put(it.toJson(context))
            }
        })
    }

    companion object {
        fun fromJson(context: Context, json: JSONObject): ProjectMetadata {
            val tracks = ArrayList<TrackInfo>().apply {
                json.getJSONArray(TRACKS).forEach {
                    add(TrackInfo.fromJson(context, it))
                }
            }
            return ProjectMetadata(tracks)
        }
    }
}
