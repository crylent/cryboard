package com.example.midicryboard.trackactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.IdRes
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

        private lateinit var detuneSlider: Croller

        private var detuneVoices = 1
        private var detuneLevel = 0f

        init {
            itemView.apply {
                onBound {
                    oscillator.apply {
                        detuneVoices = detune?.unisonVoices ?: 1
                        detuneLevel = detune?.detune ?: 0f

                        // enabled/disabled switch
                        findViewById<SwitchCompat>(R.id.oscillatorEnabled).apply {
                            isChecked = enabled
                            setOnCheckedChangeListener { _, isChecked ->
                                enabled = isChecked
                            }
                        }

                        // shape button
                        findViewById<ImageButton>(R.id.shapeButton).apply {
                            setImageResource(ShapesRecyclerAdapter.shapes[shape]!!)
                            setOnClickListener {
                                OscillatorShapeDialogFragment(shape) {
                                    setImageResource(ShapesRecyclerAdapter.shapes[it]!!)
                                    shape = it
                                }.show(
                                    (context as FragmentActivity).supportFragmentManager.beginTransaction(),
                                    OscillatorShapeDialogFragment.TAG
                                )
                            }
                        }

                        // amplitude slider
                        setupSlider(R.id.amplitudeSlider, (amplitude * AMPLITUDE_MP).roundToInt()) {
                            amplitude = it / AMPLITUDE_MP
                        }

                        // phase slider
                        setupSlider(R.id.phaseSlider, phase.toInt() + PHASE_OFFSET) {
                            phase = it - PHASE_OFFSET
                        }

                        // pitch picker
                        findViewById<NumberPicker>(R.id.pitch).apply {
                            value = pitch
                            setOnValueChangedListener { newVal, _ ->
                                pitch = newVal
                            }
                        }

                        // detune voices picker
                        findViewById<NumberPicker>(R.id.detuneVoices).apply {
                            value = detune?.unisonVoices ?: 1
                            setOnValueChangedListener { newVal, _ ->
                                detuneVoices = newVal
                                detuneSlider.isEnabled = newVal > 1
                                updateVoices()
                            }
                        }

                        // detune slider
                        detuneSlider = setupSlider(R.id.detuneSlider, (detuneLevel * DETUNE_MP).roundToInt()) {
                            detuneLevel = it / DETUNE_MP
                            updateDetune()
                        }.apply {
                            isEnabled = (detuneVoices > 1)
                        }

                        updateVoices()
                    }
                }
            }
        }

        private fun updateVoices() {
            if (this::detuneSlider.isInitialized) detuneSlider.isEnabled = (detuneVoices > 1)
            updateDetune()
        }

        private fun updateDetune() {
            oscillator.apply {
                val detuneEnabled = detuneVoices > 1 && detuneLevel > 0
                if (detuneEnabled) enableDetune(detuneVoices, detuneLevel)
                else if (detune != null) disableDetune()
            }
        }

        private fun setupSlider(@IdRes id: Int, initValue: Int, setter: (Float) -> Unit) =
            itemView.findViewById<Croller>(id).apply {
                progress = initValue
                setOnValueChangedListener { value, _ ->
                    setter(value.toFloat())
                }
            }

        companion object {
            private const val AMPLITUDE_MP = 100f
            private const val PHASE_OFFSET = 180
            private const val DETUNE_MP = 2000f // so max is 5%
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