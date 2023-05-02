package com.example.midicryboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.midilib.instrument.Instrument

class InstrumentsRecyclerAdapter(selectedCategory: String, selectedInstrument: Instrument): RecyclerView.Adapter<InstrumentsRecyclerAdapter.InstrumentViewHolder>() {
    private val instruments = MidiInstruments.instance[selectedCategory]

    class InstrumentViewHolder(itemView: View, adapter: InstrumentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        val instrumentButton: Button = itemView.findViewById<Button?>(R.id.instrumentButton).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.instrumentButton.isSelected = false
                }
                it.isSelected = true
                adapter.selectedInstrument = instrument
            }
        }

        lateinit var instrument: Instrument
    }

    private val holders = arrayListOf<InstrumentViewHolder>()

    var selectedInstrument: Instrument = selectedInstrument
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InstrumentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.instrument, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.apply {
            instrument = instruments!![position]
            instrumentButton.text = instrument!!.name
            if (instrument == selectedInstrument) instrumentButton.isSelected = true
        }
    }

    override fun getItemCount() = instruments!!.size
}