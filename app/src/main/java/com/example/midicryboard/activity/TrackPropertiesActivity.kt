package com.example.midicryboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.InstrumentsRecyclerAdapter
import com.example.midicryboard.MidiInstruments
import com.example.midicryboard.R
import com.example.midicryboard.TrackBundle

class TrackPropertiesActivity : AppCompatActivity() {
    private lateinit var instrumentsView: RecyclerView
    private val instrumentsAdapter
        get() = instrumentsView.adapter as InstrumentsRecyclerAdapter
    private var adapterInitialized = false

    private lateinit var inputBundle: Bundle
    private val trackId get() = inputBundle.getByte(TrackBundle.TRACK_ID)
    private val instrumentInput get() = inputBundle.getString(TrackBundle.INSTRUMENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_properties)

        inputBundle = intent.getBundleExtra(TrackBundle.INPUT)!!

        // Set up instruments list & categories spinner
        instrumentsView = findViewById<RecyclerView>(R.id.instrumentsList).apply {
            layoutManager = LinearLayoutManager(this@TrackPropertiesActivity)
        }
        val categoriesSpinner = findViewById<Spinner>(R.id.categories).apply {
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    instrumentsView.adapter = InstrumentsRecyclerAdapter(
                        MidiInstruments.categories[position],
                        (if (adapterInitialized) instrumentsAdapter.selectedInstrument
                        else MidiInstruments.byName(instrumentInput!!))!!
                            .also { adapterInitialized = true }
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_item).apply {
            categoriesSpinner.adapter = this
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        categoriesSpinner.setSelection(MidiInstruments.getCategoryId(
            MidiInstruments[instrumentInput!!]!!
        ))

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveProperties()
                finish()
            }
        })
    }

    private fun saveProperties() {
        setResult(RESULT_OK, Intent().apply {
            putExtra(TrackBundle.OUTPUT, Bundle().apply {
                putByte(TrackBundle.TRACK_ID, trackId)
                putString(TrackBundle.INSTRUMENT, instrumentsAdapter.selectedInstrument.name)
            })
        })
    }
}