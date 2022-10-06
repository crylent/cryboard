package com.example.midicryboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer

class MainActivity : AppCompatActivity() {

    /*private val synth = MidiSystem.getSynthesizer().apply {
        this?.open()
    }
   private val channels = synth?.channels!!*/
    private val synth = SoftSynthesizer().apply {
        open()
        //val soundbank = SF2Soundbank(assets.open("sf/Default.sf2"))
        //loadAllInstruments(soundbank)
    }
    private val midi = synth.channels[0]
    private var midiOffset = 60
    private var volume = 80

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
    //private val buttons = arrayListOf<androidx.appcompat.widget.AppCompatButton>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        synth.loadAllInstruments(SF2Soundbank(assets.open("sf/Default.sf2")))
        buttonIds.forEach {
            findViewById<androidx.appcompat.widget.AppCompatButton>(it).setOnTouchListener { v, event ->
                //super.onTouchEvent(event)
                val note = buttonIds.indexOf(it) + midiOffset
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.isPressed = true
                        midi.noteOn(note, volume)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        v.isPressed = false
                        midi.noteOff(note)
                    }
                }
                true
            }
        }
        /*buttons.forEach {
            it.setOnTouchListener { v, event ->
                val note = buttons.indexOf(it) + midiOffset
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        midi.noteOn(note, volume)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        midi.noteOff(note)
                    }
                }
                true
            }
        }*/
    }
}