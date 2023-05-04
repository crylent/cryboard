package com.example.midicryboard.activity

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

class TrackPropertiesActivity : AppCompatActivity() {
    private lateinit var instrumentsView: RecyclerView

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
                    instrumentsView.adapter = InstrumentsRecyclerAdapter(trackId, position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_item).apply {
            categoriesSpinner.adapter = this
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        categoriesSpinner.setSelection(
            Instruments.getCategoryIndex(TrackList[trackId.toInt()].instrument)!!
        )
    }
}