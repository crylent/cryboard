package com.example.midicryboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class InstrumentsRecyclerAdapter(selectedCategory: Byte, selectedInstrument: Byte): RecyclerView.Adapter<InstrumentsRecyclerAdapter.InstrumentViewHolder>() {
    private val instruments = MidiInstruments.categoryById(selectedCategory)

    class InstrumentViewHolder(itemView: View, adapter: InstrumentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        val instrumentButton: Button = itemView.findViewById<Button?>(R.id.instrumentButton).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.instrumentButton.isSelected = false
                }
                it.isSelected = true
                adapter.selectedInstrument = instrumentId
            }
        }

        var instrumentId: Byte = 0
    }

    private val holders = arrayListOf<InstrumentViewHolder>()

    var selectedInstrument: Byte = selectedInstrument
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InstrumentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.instrument, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.apply {
            instrumentButton.text = instruments[position].name
            instrumentId = instruments[position].id
            if (instrumentId == selectedInstrument) instrumentButton.isSelected = true
        }
    }

    override fun getItemCount() = instruments.size
}