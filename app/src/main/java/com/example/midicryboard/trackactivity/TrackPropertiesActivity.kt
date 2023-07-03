package com.example.midicryboard.trackactivity

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
import com.example.midicryboard.Instruments
import com.example.midicryboard.R
import com.example.midicryboard.TrackList
import com.example.midicryboard.TrackParams
import com.example.midilib.instrument.Instrument
import com.sdsmdg.harjot.crollerTest.Croller
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.round

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
                addAll(Instruments.categoryNames)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        val instrument = TrackList[trackId.toInt()].instrument
        categoriesSpinner.setSelection(
            Instruments.getCategoryIndex(instrument)!!
        )

        envelopeCanvas = findViewById(R.id.envelopeCanvas)
        viewInstrument(instrument)

        // Create custom instrument dialog
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

    private fun setupCroller(id: Int, value: Float, power: Float, setter: (Float)->Unit) {
        findViewById<Croller>(id).apply {
            max = ADSR_POINTS_NUMBER
            label = value.toString()
            progress = crollerValueToProgress(value, power)
            setOnProgressChangedListener {
                val time = crollerProgressToValue(it, power)
                label = time.toString()
                setter(time)
            }
        }
    }

    private fun crollerProgressToValue(value: Int, power: Float) = round(
        (value - 1f).pow(power) * ADSR_FIRST_STEP * ADSR_ROUNDING_FACTOR
    ) / ADSR_ROUNDING_FACTOR

    private fun crollerValueToProgress(time: Float, power: Float) = ceil(
        (time / ADSR_FIRST_STEP).pow(1/power)
    ).toInt()

    fun viewInstrument(instrument: Instrument) {
        envelopeCanvas.instrument = instrument
        instrument.apply {
            setupCroller(R.id.attackSlider, attack, ADR_TIME_POWER) { attack = it }
            setupCroller(R.id.decaySlider, decay, ADR_TIME_POWER) { decay = it }
            setupCroller(R.id.sustainSlider, sustain, SUSTAIN_POWER) { sustain = it }
            setupCroller(R.id.releaseSlider, release, ADR_TIME_POWER) { release = it }
            setupCroller(R.id.attackFormSlider, attackSharpness, ADR_SHARPNESS_POWER) { attackSharpness = it }
            setupCroller(R.id.decayFormSlider, decaySharpness, ADR_SHARPNESS_POWER) { decaySharpness = it }
            setupCroller(R.id.releaseFormSlider, releaseSharpness, ADR_SHARPNESS_POWER) { releaseSharpness = it }
        }
    }

    companion object {
        private const val ADSR_POINTS_NUMBER = 200
        private const val ADR_MAX_TIME = 50f
        private const val ADR_MAX_SHARPNESS = 20f
        private const val MAX_SUSTAIN = 1f
        private const val ADSR_FIRST_STEP = 0.0005f
        private val ADR_TIME_POWER = log(ADR_MAX_TIME / ADSR_FIRST_STEP, ADSR_POINTS_NUMBER - 1f)
        private val ADR_SHARPNESS_POWER = log(ADR_MAX_SHARPNESS / ADSR_FIRST_STEP, ADSR_POINTS_NUMBER - 1f)
        private val SUSTAIN_POWER = log(MAX_SUSTAIN / ADSR_FIRST_STEP, ADSR_POINTS_NUMBER - 1f)
        private const val ADSR_DECIMAL_DIGITS = 3
        private val ADSR_ROUNDING_FACTOR = 10f.pow(ADSR_DECIMAL_DIGITS)
    }
}