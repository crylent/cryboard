package com.example.midicryboard.trackactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

        private val pitchUp = setupButton(R.id.pitchUpButton) {
            oscillator.pitch += 1
            updatePitch()
        }

        private val pitchDown = setupButton(R.id.pitchDownButton) {
            oscillator.pitch -= 1
            updatePitch()
        }

        private val pitchText = itemView.findViewById<TextView>(R.id.pitchText)

        private val voicesUp = setupButton(R.id.voicesUpButton) {
            if (detuneVoices < MAX_UNISON_VOICES) detuneVoices += 1
            updateVoices()
        }

        private val voicesDown = setupButton(R.id.voicesDownButton) {
            if (detuneVoices > MIN_UNISON_VOICES) detuneVoices -= 1
            updateVoices()
        }

        private val voicesText = itemView.findViewById<TextView>(R.id.voicesText)

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

                        // detune slider
                        detuneSlider = setupSlider(R.id.detuneSlider, (detuneLevel * DETUNE_MP).roundToInt()) {
                            detuneLevel = it / DETUNE_MP
                            updateDetune()
                        }.apply {
                            isEnabled = (detuneVoices > 1)
                        }

                        updatePitch()
                        updateVoices()
                    }
                }
            }
        }

        private fun updatePitch() {
            oscillator.pitch.also { pitch ->
                pitchText.text = pitch.toString()
                pitchUp.isEnabled = (pitch < MAX_PITCH)
                pitchDown.isEnabled = (pitch > MIN_PITCH)
            }
        }

        private fun updateVoices() {
            voicesText.text = detuneVoices.toString()
            voicesUp.isEnabled = (detuneVoices < MAX_UNISON_VOICES)
            voicesDown.isEnabled = (detuneVoices > MIN_UNISON_VOICES)
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

        private fun setupButton(@IdRes id: Int, onClick: () -> Unit) =
            itemView.findViewById<ImageButton>(id).apply {
                setOnClickListener { onClick() }
            }

        companion object {
            private const val AMPLITUDE_MP = 100f
            private const val PHASE_OFFSET = 180
            private const val DETUNE_MP = 2000f // so max is 5%
            private const val MIN_PITCH = -60
            private const val MAX_PITCH = 60
            private const val MIN_UNISON_VOICES = 1
            private const val MAX_UNISON_VOICES = 8
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