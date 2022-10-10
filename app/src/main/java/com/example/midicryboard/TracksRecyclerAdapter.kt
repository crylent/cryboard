package com.example.midicryboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.activity.*

class TracksRecyclerAdapter(private val trackNameList: List<String> = listOf()): RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View, adapter: TracksRecyclerAdapter, trackId: Byte): RecyclerView.ViewHolder(itemView) {
        val useTrackButton: ImageButton = itemView.findViewById<ImageButton?>(R.id.useTrack).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.useTrackButton.isSelected = false
                }
                it.isSelected = true
                adapter.selectedTrack = adapterPosition.toByte()
                Log.println(Log.DEBUG, "selectedTrack", adapter.selectedTrack.toString())
            }
        }

        val trackNameButton: Button = itemView.findViewById<Button?>(R.id.trackName).apply {
            setOnClickListener {
                (it.context as MainActivity).openTrackProperties(trackId)
            }
        }
    }

    private val holders = arrayListOf<TrackViewHolder>()

    var selectedTrack: Byte = 0
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false),
        this,
        holders.size.toByte()
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.useTrackButton.isSelected = (selectedTrack == position.toByte())
        holder.trackNameButton.text = trackNameList[position]
    }

    override fun getItemCount() = trackNameList.size

    fun updateItem(trackId: Byte, newName: String) {
        holders[trackId.toInt()].trackNameButton.text = newName
    }
}