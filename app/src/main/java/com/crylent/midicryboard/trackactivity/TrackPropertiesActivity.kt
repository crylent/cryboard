package com.crylent.midicryboard.trackactivity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.crylent.midicryboard.Instruments
import com.crylent.midicryboard.R
import com.crylent.midicryboard.TrackList
import com.crylent.midicryboard.TrackParams
import com.crylent.midicryboard.projectactivities.InstrumentTabsAdapter
import com.crylent.midilib.instrument.Instrument
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TrackPropertiesActivity : AppCompatActivity() {
    private lateinit var instrumentsView: RecyclerView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tabsAdapter: InstrumentTabsAdapter

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
                addAll(Instruments.categoryNames)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        val instrument = TrackList[trackId.toInt()].instrument
        categoriesSpinner.setSelection(
            Instruments.getCategoryIndex(instrument)!!
        )

        tabsAdapter = InstrumentTabsAdapter(instrument, supportFragmentManager, lifecycle)
        viewPager = findViewById<ViewPager2>(R.id.viewPager).apply {
            adapter = tabsAdapter
        }
        tabLayout = findViewById(R.id.propertiesTabs)
        val tabsArray = arrayOf(
            getString(R.string.envelope),
            getString(R.string.effects),
            getString(R.string.oscillators)
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        // Custom instrument dialog
        findViewById<Button>(R.id.createCustomButton).apply {
            setOnClickListener {
                val categoryId = categoriesSpinner.selectedItemPosition
                CustomInstrumentDialogFragment(categoryId) {
                    // On created callback
                    instrumentsView.apply {
                        val instrumentIndex = Instruments.getCategoryById(categoryId).items.size - 1
                        (adapter as InstrumentsRecyclerAdapter).apply {
                            notifyItemInserted(instrumentIndex)
                            selectInstrument(instrumentIndex)
                        }
                        scrollToPosition(instrumentIndex)
                    }
                }.show(
                    supportFragmentManager,
                    CustomInstrumentDialogFragment.TAG
                )
            }
        }
    }

    fun viewInstrument(instrument: Instrument) {
        tabsAdapter.updateFragments(instrument)
    }
}