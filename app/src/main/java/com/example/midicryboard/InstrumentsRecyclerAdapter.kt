package com.example.midicryboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class InstrumentsRecyclerAdapter(private val trackId: Byte, private val selectedCategory: Int): RecyclerView.Adapter<InstrumentsRecyclerAdapter.InstrumentViewHolder>() {
    private val category = Instruments.instance[selectedCategory]

    class InstrumentViewHolder(itemView: View, adapter: InstrumentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        val instrumentButton: Button = itemView.findViewById<Button?>(R.id.instrumentButton).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.instrumentButton.isSelected = false
                }
                it.isSelected = true
                TrackList.setTrackInfo(adapter.trackId, adapter.category[instrumentIndex])
            }
        }

        var instrumentIndex = 0
    }

    private val holders = arrayListOf<InstrumentViewHolder>()

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
            if (Instruments[selectedCategory][position] == TrackList[trackId.toInt()].instrument)
                instrumentButton.isSelected = true
        }
    }

    override fun getItemCount() = category.items.size
}