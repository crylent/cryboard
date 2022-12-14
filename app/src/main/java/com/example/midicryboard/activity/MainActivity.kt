package com.example.midicryboard.activity

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.*
import com.sdsmdg.harjot.crollerTest.Croller

const val DEFAULT_MIDI_OFFSET = 60
const val MAX_OCTAVE_UP = 2
const val MAX_OCTAVE_DOWN = -3

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var resources: Resources
    }

    private var octave = 0
    private val midiOffset
        get() = (DEFAULT_MIDI_OFFSET + octave * 12).toByte()

    private val keyboardIds = arrayListOf(
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

    private lateinit var playPauseButton: ImageButton
    private lateinit var recordButton: ImageButton
    private lateinit var stopButton: ImageButton

    private var startRecordingOnPlay = false

    private lateinit var tracks: TrackList
    private lateinit var tracksAdapter: TracksRecyclerAdapter
    val selectedTrack
        get() = tracksAdapter.selectedTrack

    private lateinit var tracksCanvas: TracksCanvas
    lateinit var tracksScrollView: HorizontalScrollView
        private set

    private lateinit var trackPropertiesLauncher: ActivityResultLauncher<Bundle>

    init {
        Midi.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Companion.resources = resources

        tracks = TrackList()
        tracksAdapter = TracksRecyclerAdapter(tracks.namesList)

        // Set up volume slider
        findViewById<Croller>(R.id.volumeSlider).apply {
            progress = Midi.volume.toInt()
            setOnProgressChangedListener {
                Midi.volume = it.toByte()
            }
        }

        shiftOctave(0) // For setting text octaveShift
        // Add listeners for octave shifter
        findViewById<Button>(R.id.octaveDown).setOnClickListener {
            shiftOctave(-1)
        }
        findViewById<Button>(R.id.octaveUp).setOnClickListener {
            shiftOctave(1)
        }

        // Set up metronome
        findViewById<EditText>(R.id.editTempo).apply {
            stopMetronomeOnFocus()
            setEditListener {
                val tempo = text.toString().toIntOrNull()
                if (tempo != null && tempo in 30..300) {
                    Metronome.tempo = tempo
                    Midi.updateTempoAndSignature()
                    clearFocus()
                    false // hide keyboard
                }
                else {
                    setText(Metronome.tempo.toString())
                    true // don't hide keyboard
                }
            }
        }
        findViewById<EditText>(R.id.editTimeSignature).apply {
            stopMetronomeOnFocus()
            setEditListener {
                TimeSignature.fromString(text.toString()).run {
                    if (this != null) {
                        Metronome.signature = this
                        Midi.updateTempoAndSignature()
                        clearFocus()
                        false // hide keyboard
                    }
                    else {
                        setText(Metronome.signature.toString())
                        true // don't hide keyboard
                    }
                }
            }
        }

        // Set up controls
        playPauseButton = findViewById<ImageButton>(R.id.playPauseButton).apply {
            setOnClickListener {
                if (isActivated) {
                    Metronome.stop()
                    Midi.pausePlayback()
                }
                else {
                    Metronome.start()
                    if (startRecordingOnPlay) startRecording()
                    else Midi.startPlayback()
                }
                isActivated = !isActivated

            }
        }
        recordButton = findViewById<ImageButton>(R.id.recordButton).apply {
            setOnClickListener {
                if (isActivated) {
                    Midi.stopRecording()
                    startRecordingOnPlay = false
                }
                else {
                    if (Metronome.running) startRecording()
                    else startRecordingOnPlay = true
                }
                isActivated = !isActivated
            }
        }
        stopButton = findViewById<ImageButton>(R.id.stopButton).apply {
            setOnClickListener {
                Metronome.stop()
                Midi.stopPlayback()
                recordButton.isActivated = false
                playPauseButton.isActivated = false
            }
        }

        // Set up track list
        findViewById<RecyclerView?>(R.id.trackList).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tracksAdapter
        }
        trackPropertiesLauncher = registerForActivityResult(TrackPropertiesActivityContract()) { result ->
            result!!.apply {
                val trackId = getByte(TrackBundle.TRACK_ID)
                tracks.setTrackInfo(
                    trackId,
                    getByte(TrackBundle.INSTRUMENT)
                )
                tracksAdapter.updateItem(trackId, tracks.getTrackName(trackId))
            }
        }
        tracksCanvas = findViewById(R.id.tracksCanvas)
        tracksScrollView = findViewById(R.id.tracksScrollView)

        // Add listeners for keyboard
        keyboardIds.forEach {
            findViewById<androidx.appcompat.widget.AppCompatButton>(it).setOnTouchListener { v, event ->
                if (startRecordingOnPlay) {
                    startRecording()
                    Metronome.start()
                    playPauseButton.isActivated = true
                }
                val note = (keyboardIds.indexOf(it) + midiOffset).toByte()
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        v.isPressed = true
                        Midi.noteOn(note, selectedTrack)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        v.isPressed = false
                        Midi.noteOff(note, selectedTrack)
                    }
                }
                true
            }
        }
    }

    private fun EditText.setEditListener(lambda: () -> Boolean) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                lambda()
            }
            else false
        }
        setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                lambda()
                true
            }
            else false
        }
    }

    private fun EditText.stopMetronomeOnFocus() {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Metronome.stop()
                playPauseButton.isActivated = false
            }
        }
    }

    private fun shiftOctave(shift: Int) {
        octave += shift
        if (octave > MAX_OCTAVE_UP) octave = MAX_OCTAVE_UP
        else if (octave < MAX_OCTAVE_DOWN) octave = MAX_OCTAVE_DOWN
        findViewById<TextView>(R.id.octaveShift).text = octave.toString()
    }

    fun openTrackProperties(trackId: Byte) {
        trackPropertiesLauncher.launch(Bundle().apply {
            putByte(TrackBundle.TRACK_ID, trackId)
            putByte(TrackBundle.INSTRUMENT, tracks.getInstrumentId(trackId))
        })
    }

    private fun startRecording() {
        startRecordingOnPlay = false // reset it
        Midi.clearTrack(selectedTrack)
        Midi.startRecording()
    }

    override fun onDestroy() {
        super.onDestroy()
        Midi.stop()
    }
}