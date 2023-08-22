package com.example.midicryboard.projectactivities

import android.util.Log
import android.view.View
import com.example.midicryboard.R
import com.example.midicryboard.trackactivity.EnvelopeCanvas
import com.example.midilib.instrument.Instrument
import com.sdsmdg.harjot.crollerTest.Croller
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

class EnvelopeFragment(instrument: Instrument) : InstrumentTabFragment(instrument) {
    override val layout: Int = R.layout.fragment_envelope
    private lateinit var envelopeCanvas: EnvelopeCanvas

    override fun View.createView() {
        envelopeCanvas = findViewById(R.id.envelopeCanvas)
        updateView()
    }

    override fun View.updateView() {
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
    }

    private enum class CrollerMode {
        POWER_DEFINED,
        FIRST_STEP_DEFINED,
        MID_POINT_DEFINED
    }

    private fun View.setupCroller(
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
            setOnValueChangedListener { value, fromUser ->
                if (!fromUser) return@setOnValueChangedListener
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

    companion object {
        private const val ADSR_POINTS_NUMBER = 200
        private const val ADR_MIN_TIME = 0f
        private const val ADR_MAX_TIME = 50f
        private const val ADR_MIN_SHARPNESS = 0.1f
        private const val ADR_MAX_SHARPNESS = 10f
        private const val MIN_SUSTAIN = 0f
        private const val MAX_SUSTAIN = 1f
        private const val ADR_TIME_FIRST_STEP = 0.0005f
        private const val SUSTAIN_POWER = 1f
    }
}