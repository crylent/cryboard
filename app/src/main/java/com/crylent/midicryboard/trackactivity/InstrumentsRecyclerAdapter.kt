package com.crylent.midicryboard.trackactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.crylent.midicryboard.Instruments
import com.crylent.midicryboard.R
import com.crylent.midicryboard.TrackList

class InstrumentsRecyclerAdapter(
    private val activity: TrackPropertiesActivity,
    private val trackId: Byte,
    private val selectedCategory: Int
): RecyclerView.Adapter<InstrumentsRecyclerAdapter.InstrumentViewHolder>() {
    private val category = Instruments.getCategoryById(selectedCategory)

    class InstrumentViewHolder(itemView: View, adapter: InstrumentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        val instrumentButton: Button = itemView.findViewById<Button?>(R.id.instrumentButton).apply {
            setOnClickListener {
                adapter.selectInstrument(instrumentIndex)
            }
        }

        var instrumentIndex = 0
    }

    private val holders = arrayListOf<InstrumentViewHolder>()

    fun selectInstrument(instrumentIndex: Int) {
        for (i in holders.indices) {
            holders[i].instrumentButton.isSelected = (i == instrumentIndex)
        }
        val instrument = category[instrumentIndex]
        TrackList.setTrackInfo(trackId, instrument)
        activity.viewInstrument(instrument)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InstrumentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.instrument, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.apply {
            instrumentIndex = position
            instrumentButton.text = category[position].name
            if (Instruments.getCategoryById(selectedCategory)[position] == TrackList[trackId.toInt()].instrument)
                instrumentButton.isSelected = true
        }
    }

    override fun getItemCount() = category.items.size
}