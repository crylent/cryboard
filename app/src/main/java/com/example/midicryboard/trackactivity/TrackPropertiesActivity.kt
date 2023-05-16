package com.example.midicryboard.trackactivity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.*
import com.example.midilib.instrument.Instrument

class TrackPropertiesActivity : AppCompatActivity() {
    private lateinit var instrumentsView: RecyclerView
    private lateinit var envelopeCanvas: EnvelopeCanvas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_properties)

        val trackId = intent.getByteExtra(TrackParams.TRACK_ID, 0)

        // Set up instruments list & categories spinner
        instrumentsView = findViewById<RecyclerView>(R.id.instrumentsList).apply {
            layoutManager = LinearLayoutManager(this@TrackPropertiesActivity)
        }
        val categoriesSpinner = findViewById<Spinner>(R.id.categories).apply {
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    instrumentsView.adapter = InstrumentsRecyclerAdapter(
                        this@TrackPropertiesActivity,
                        trackId, position
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            adapter = ArrayAdapter<String>(
                this@TrackPropertiesActivity, android.R.layout.simple_spinner_item
            ).apply {
                addAll(Instruments.categories)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        val instrument = TrackList[trackId.toInt()].instrument
        categoriesSpinner.setSelection(
            Instruments.getCategoryIndex(instrument)!!
        )

        envelopeCanvas = findViewById(R.id.envelopeCanvas)
        viewInstrument(instrument)
    }

    fun viewInstrument(instrument: Instrument) {
        envelopeCanvas.instrument = instrument
    }
}