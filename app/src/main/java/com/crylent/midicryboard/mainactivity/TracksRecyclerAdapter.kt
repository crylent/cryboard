package com.crylent.midicryboard.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.crylent.midicryboard.Midi
import com.crylent.midicryboard.R
import com.crylent.midicryboard.TrackList

class TracksRecyclerAdapter: RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View, adapter: TracksRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        var trackId: Byte = 0
            set(value) {
                field = value
                trackNameButton.text = TrackList.namesList[trackId.toInt()]
            }

        val useTrackButton: ImageButton = itemView.findViewById<ImageButton?>(R.id.useTrack).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.useTrackButton.isSelected = false
                }
                it.isSelected = true
                Midi.allNotesOff(adapter.selectedTrack)
                adapter.selectedTrack = adapterPosition.toByte()
            }
        }

        private val trackNameButton: Button = itemView.findViewById<Button?>(R.id.trackName).apply {
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
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.apply {
            trackId = position.toByte()
            useTrackButton.isSelected = (selectedTrack == trackId)
        }
    }

    override fun getItemCount() = TrackList.namesList.size

    fun updateItem(trackId: Byte) {
        notifyItemChanged(trackId.toInt())
    }

    fun resetItems() {
        notifyItemRangeChanged(0, itemCount)
    }
}