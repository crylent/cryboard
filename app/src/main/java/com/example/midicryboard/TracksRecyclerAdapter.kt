package com.example.midicryboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.activity.MainActivity

class TracksRecyclerAdapter: RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View, adapter: TracksRecyclerAdapter, trackId: Byte): RecyclerView.ViewHolder(itemView) {
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
        holder.apply {
            useTrackButton.isSelected = (selectedTrack == position.toByte())
            trackNameButton.text = TrackList.namesList[position]
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