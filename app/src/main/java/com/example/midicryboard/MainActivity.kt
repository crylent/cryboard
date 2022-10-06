package com.example.midicryboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import org.billthefarmer.mididriver.MidiDriver

const val DEFAULT_MIDI_OFFSET = 60
const val MAX_OCTAVE_UP = 2
const val MAX_OCTAVE_DOWN = -3

class MainActivity : AppCompatActivity() {
    private val midi = MidiDriver.getInstance()
    private var octave = 0
    private val midiOffset
        get() = (DEFAULT_MIDI_OFFSET + octave * 12).toByte()
    private var volume = 100.toByte()

    private val buttonIds = arrayListOf(
        R.id.button_c1,
        R.id.button_cd1,
        R.id.button_d1,
        R.id.button_dd1,
        R.id.button_e1,
        R.id.button_f1,
        R.id.button_fd1,
        R.id.button_g1,
        R.id.button_gd1,
        R.id.button_a1,
        R.id.button_ad1,
        R.id.button_b1,
        R.id.button_c2,
        R.id.button_cd2,
        R.id.button_d2,
        R.id.button_dd2,
        R.id.button_e2,
        R.id.button_f2,
        R.id.button_fd2,
        R.id.button_g2,
        R.id.button_gd2,
        R.id.button_a2,
        R.id.button_ad2,
        R.id.button_b2,
        R.id.button_c3
    )

    private fun noteEvent(note: Int, action: Int) {
        val event = ByteArray(3)
        event[0] = action.toByte()
        event[1] = note.toByte()
        event[2] = volume
        midi.write(event)
    }

    private fun noteOn(note: Int) {
        noteEvent(note, 0x90 or 0x00)
    }
    private fun noteOff(note: Int) {
        noteEvent(note, 0x80 or 0x00)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        midi.start()

        // Add listeners for keyboard
        buttonIds.forEach {
            findViewById<androidx.appcompat.widget.AppCompatButton>(it).setOnTouchListener { v, event ->
                val note = buttonIds.indexOf(it) + midiOffset
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        v.isPressed = true
                        noteOn(note)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        v.isPressed = false
                        noteOff(note)
                    }
                }
                true
            }
        }

        // Add listeners for octave shifter
        findViewById<Button>(R.id.octaveDown).setOnClickListener {
            shiftOctave(-1)
        }
        findViewById<Button>(R.id.octaveUp).setOnClickListener {
            shiftOctave(1)
        }
    }

    private fun shiftOctave(shift: Int) {
        octave += shift
        if (octave > MAX_OCTAVE_UP) octave = MAX_OCTAVE_UP
        else if (octave < MAX_OCTAVE_DOWN) octave = MAX_OCTAVE_DOWN
        findViewById<TextView>(R.id.octaveShift).text = octave.toString()
    }
}