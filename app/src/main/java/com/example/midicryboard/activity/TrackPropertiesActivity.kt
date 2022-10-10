package com.example.midicryboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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
    private fun getFromInput(key: String) = inputBundle.getByte(key)

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
                        position.toByte(),
                        if (adapterInitialized) instrumentsAdapter.selectedInstrument
                        else getFromInput(TrackBundle.INSTRUMENT).also { adapterInitialized = true }
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_item).apply {
            categoriesSpinner.adapter = this
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        categoriesSpinner.setSelection(MidiInstruments.getCategoryId(getFromInput(TrackBundle.INSTRUMENT)).toInt())

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveProperties()
                finish()
            }
        })
    }

    private fun saveProperties() {
        Toast.makeText(this, instrumentsAdapter.selectedInstrument.toString(), Toast.LENGTH_LONG).show()
        setResult(RESULT_OK, Intent().apply {
            putExtra(TrackBundle.OUTPUT, Bundle().apply {
                putByte(TrackBundle.TRACK_ID, getFromInput(TrackBundle.TRACK_ID))
                putByte(TrackBundle.INSTRUMENT, instrumentsAdapter.selectedInstrument)
            })
        })
    }
}