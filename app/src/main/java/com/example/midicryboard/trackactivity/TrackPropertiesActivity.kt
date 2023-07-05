package com.example.midicryboard.trackactivity

import android.os.Bundle
import android.util.Log
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
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

class TrackPropertiesActivity : AppCompatActivity() {
    private lateinit var instrumentsView: RecyclerView
    private lateinit var envelopeCanvas: EnvelopeCanvas
    //private lateinit var oscillatorsView: RecyclerView

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

        // Oscillators for synth instrument
        /*oscillatorsView = findViewById<RecyclerView>(R.id.oscillators).apply {
            layoutManager = LinearLayoutManager(this@TrackPropertiesActivity)
        }*/

        viewInstrument(instrument)

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

    private enum class CrollerMode {
        POWER_DEFINED,
        FIRST_STEP_DEFINED,
        MID_POINT_DEFINED
    }

    private fun setupCroller(
        id: Int,
        value: Float,
        minValue: Float, maxValue: Float,
        crollerMode: CrollerMode,
        crollerModeParam: Float,
        setter: (Float)->Unit
    ) {
        findViewById<Croller>(id).apply {
            val range = maxValue - minValue
            val power: Float
            val firstStep: Float
            when (crollerMode) {
                CrollerMode.POWER_DEFINED -> {
                    power = crollerModeParam
                    firstStep = range / (ADSR_POINTS_NUMBER - 1f).pow(power)
                }
                CrollerMode.FIRST_STEP_DEFINED -> {
                    firstStep = crollerModeParam
                    power = log(range / firstStep, ADSR_POINTS_NUMBER - 1f)
                }
                CrollerMode.MID_POINT_DEFINED -> {
                    @Suppress("UnnecessaryVariable")
                    val midPointValue = crollerModeParam
                    val midPointNumber =
                        if (ADSR_POINTS_NUMBER % 2 == 0) ADSR_POINTS_NUMBER / 2
                        else (ADSR_POINTS_NUMBER + 1) / 2
                    power = log(range / midPointValue, (ADSR_POINTS_NUMBER - 1) / (midPointNumber - 1f))
                    firstStep = range / (ADSR_POINTS_NUMBER - 1f).pow(power)
                }
            }
            max = ADSR_POINTS_NUMBER
            label = value.toString()
            progress = crollerValueToProgress(value, power, firstStep)
            setOnValueChangedListener { value, _ ->
                val time = minValue + crollerProgressToValue(value, power, firstStep)
                label = decimalFormat.format(time)
                Log.d("croller", time.toString())
                setter(time)
            }
        }
    }

    private val decimalFormat = DecimalFormat.getNumberInstance().apply {
        maximumFractionDigits = 4
    }

    private fun crollerProgressToValue(progress: Int, power: Float, step: Float) =
        (progress - 1f).pow(power) * step

    private fun crollerValueToProgress(value: Float, power: Float, step: Float) = ceil(
        (value / step).pow(1/power)
    ).toInt()

    fun viewInstrument(instrument: Instrument) {
        envelopeCanvas.instrument = instrument
        instrument.apply {
            setupCroller(
                R.id.attackSlider, attack, ADR_MIN_TIME, ADR_MAX_TIME,
                CrollerMode.FIRST_STEP_DEFINED, ADR_TIME_FIRST_STEP
            ) { attack = it }
            setupCroller(
                R.id.decaySlider, decay, ADR_MIN_TIME, ADR_MAX_TIME,
                CrollerMode.FIRST_STEP_DEFINED, ADR_TIME_FIRST_STEP
            ) { decay = it }
            setupCroller(
                R.id.sustainSlider, sustain, MIN_SUSTAIN, MAX_SUSTAIN,
                CrollerMode.POWER_DEFINED, SUSTAIN_POWER
            ) { sustain = it }
            setupCroller(
                R.id.releaseSlider, release, ADR_MIN_TIME, ADR_MAX_TIME,
                CrollerMode.FIRST_STEP_DEFINED, ADR_TIME_FIRST_STEP
            ) { release = it }
            setupCroller(
                R.id.attackFormSlider, attackSharpness, ADR_MIN_SHARPNESS, ADR_MAX_SHARPNESS,
                CrollerMode.MID_POINT_DEFINED, 1f
            ) { attackSharpness = it }
            setupCroller(
                R.id.decayFormSlider, decaySharpness, ADR_MIN_SHARPNESS, ADR_MAX_SHARPNESS,
                CrollerMode.MID_POINT_DEFINED, 1f
            ) { decaySharpness = it }
            setupCroller(
                R.id.releaseFormSlider, releaseSharpness, ADR_MIN_SHARPNESS, ADR_MAX_SHARPNESS,
                CrollerMode.MID_POINT_DEFINED, 1f
            ) { releaseSharpness = it }
        }
        /*oscillatorsView.apply {
            adapter = OscillatorsRecyclerAdapter(this, instrument)
        }*/
    }

    companion object {
        private const val ADSR_POINTS_NUMBER = 200
        //private val ADSR_CENTRAL_POINT = if (ADSR_POINTS_NUMBER % 2 == 0) ADSR_POINTS_NUMBER / 2 else (ADSR_POINTS_NUMBER + 1) / 2
        private const val ADR_MIN_TIME = 0f
        private const val ADR_MAX_TIME = 50f
        private const val ADR_MIN_SHARPNESS = 0.1f
        private const val ADR_MAX_SHARPNESS = 10f
        //private const val ADR_SHARPNESS_RANGE = ADR_MAX_SHARPNESS - ADR_MIN_SHARPNESS
        private const val MIN_SUSTAIN = 0f
        private const val MAX_SUSTAIN = 1f
        private const val ADR_TIME_FIRST_STEP = 0.0005f
        //private const val SUSTAIN_FIRST_STEP = MAX_SUSTAIN / ADSR_POINTS_NUMBER
        //private val ADR_TIME_POWER = log(ADR_MAX_TIME / ADR_TIME_FIRST_STEP, ADSR_POINTS_NUMBER - 1f)
        //private val ADR_SHARPNESS_POWER = log(ADR_SHARPNESS_RANGE, (ADSR_POINTS_NUMBER - 1) / (ADSR_CENTRAL_POINT - 1f))
        //private val ADR_SHARPNESS_FIRST_STEP = 1f / (ADSR_CENTRAL_POINT - 1f).pow(ADR_SHARPNESS_POWER)
        private const val SUSTAIN_POWER = 1f
        //private const val ADSR_DECIMAL_DIGITS = 4
        //private val ADSR_ROUNDING_FACTOR = 10f.pow(ADSR_DECIMAL_DIGITS)
    }
}