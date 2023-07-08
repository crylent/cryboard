package com.example.midicryboard.trackactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.HolderWithCallback
import com.example.midicryboard.R
import com.example.midilib.Oscillator
import com.example.midilib.instrument.SynthInstrument
import com.sdsmdg.harjot.crollerTest.Croller
import kotlin.math.roundToInt

class OscillatorsRecyclerAdapter(
    private val instrument: SynthInstrument
): RecyclerView.Adapter<OscillatorsRecyclerAdapter.OscillatorViewHolder>() {
    class OscillatorViewHolder(itemView: View, private val adapter: OscillatorsRecyclerAdapter): HolderWithCallback(itemView) {
        var oscillatorIndex = 0
            set(value) {
                field = value
                oscillator = adapter.instrument.getOscillator(value)
                invokeCallback()
            }

        private lateinit var oscillator: Oscillator

        private val pitchText: TextView = itemView.findViewById<TextView>(R.id.pitchText).apply {
            onBound {
                updatePitchText()
            }
        }

        private fun updatePitchText() {
            pitchText.text = oscillator.pitch.toString()
        }

        private fun setupSlider(slider: Croller, initValue: Int, setter: (Float) -> Unit) {
            slider.apply {
                progress = initValue
                setOnValueChangedListener { value, _ ->
                    setter(value.toFloat())
                }
            }
        }

        init {
            itemView.apply {
                onBound {
                    // enabled/disabled switch
                    findViewById<SwitchCompat>(R.id.oscillatorEnabled).apply {
                        isChecked = oscillator.enabled
                        setOnCheckedChangeListener { _, isChecked ->
                            oscillator.enabled = isChecked
                        }
                    }

                    // shape button
                    findViewById<ImageButton>(R.id.shapeButton).apply {
                        setImageResource(ShapesRecyclerAdapter.shapes[oscillator.shape]!!)
                        setOnClickListener {
                            OscillatorShapeDialogFragment(oscillator.shape) {
                                setImageResource(ShapesRecyclerAdapter.shapes[it]!!)
                                oscillator.shape = it
                            }.show(
                                (context as FragmentActivity).supportFragmentManager.beginTransaction(),
                                OscillatorShapeDialogFragment.TAG
                            )
                        }
                    }

                    // amplitude slider
                    findViewById<Croller>(R.id.amplitudeSlider).also { croller ->
                        oscillator.apply {
                            setupSlider(croller, (amplitude * AMPLITUDE_FACTOR).roundToInt()) {
                                amplitude = it / AMPLITUDE_FACTOR
                            }
                        }
                    }

                    // phase slider
                    findViewById<Croller>(R.id.phaseSlider).also { croller ->
                        oscillator.apply {
                            setupSlider(croller, phase.toInt() + PHASE_OFFSET) {
                                phase = it - PHASE_OFFSET
                            }
                        }
                    }

                    // pitch up button
                    findViewById<ImageButton>(R.id.pitchUpButton).apply {
                        setOnClickListener {
                            oscillator.pitch += 1
                            updatePitchText()
                        }
                    }

                    // pitch down button
                    findViewById<ImageButton>(R.id.pitchDownButton).apply {
                        setOnClickListener {
                            oscillator.pitch -= 1
                            updatePitchText()
                        }
                    }
                }
            }
        }

        companion object {
            private const val AMPLITUDE_FACTOR = 100f
            private const val PHASE_OFFSET = 180
        }
    }

    private val holders = arrayListOf<OscillatorViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OscillatorViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.oscillator, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: OscillatorViewHolder, position: Int) {
        holder.apply {
            oscillatorIndex = position
        }
    }

    override fun getItemCount() = instrument.oscCount
}