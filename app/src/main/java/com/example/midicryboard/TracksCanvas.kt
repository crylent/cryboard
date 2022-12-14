package com.example.midicryboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.plus
import com.example.midicryboard.activity.MainActivity
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn

class TracksCanvas(context: Context, attrs: AttributeSet): SurfaceView(context, attrs), SurfaceHolder.Callback {
    private val p = Paint().apply { color = Color.MAGENTA }
    private val markup = Paint().apply { color = Color.GRAY }
    private val pointer = Paint().apply { color = Color.RED }
    private val backgroundColor = when(Theme.getTheme(context)) {
        Theme.DARK -> Color.BLACK
        Theme.LIGHT -> Color.WHITE
    }

    init {
        holder.addCallback(this)
    }

    private val activity = context as MainActivity

    private var fullRedraw = true

    fun callFullRedraw() {
        fullRedraw = true
    }

    private val regions = MutableList(TRACKS_NUMBER) { Region() }

    private var maxPointerPosition = 0

    fun redraw(canvas: Canvas) {
        //var newWidth = width
        canvas.apply {
            drawColor(backgroundColor)

            val selected = activity.selectedTrack
            regions[selected.toInt()].setEmpty()
            // Draw notes
            var notesOn = 0
            var noteOnPos = 0f
            Midi.processEvents(
                if (fullRedraw) Midi.ALL_TRACKS
                else selected
            ) { trackId, event ->
                val eventPos = event.tick * WIDTH_MULTIPLIER
                //if (newWidth + EXTRA_WIDTH < eventPos) newWidth = eventPos.toInt() + EXTRA_WIDTH
                if (event is NoteOn) {
                    if (notesOn == 0) noteOnPos = eventPos
                    notesOn += 1
                }
                else if (event is NoteOff) {
                    notesOn -= 1
                    if (notesOn == 0) {
                        val x = trackX(trackId)
                        regions[trackId.toInt()] = regions[trackId.toInt()].plus(Rect(
                            noteOnPos.toInt(), x.first.toInt(),
                            eventPos.toInt(), x.second.toInt()
                        ))
                    }
                }
            }
            fullRedraw = false

            regions.forEach {
                drawPath(it.boundaryPath, p)
            }

            // Draw bar lines
            var barPos = 0f
            val barDelta = Metronome.period * Metronome.signature.beats * WIDTH_MULTIPLIER
            while (barPos < width) {
                barPos += barDelta
                drawLine(barPos, 0f, barPos, height.toFloat(), markup)
            }

            // Draw pointer
            val pointerPos = Midi.time * WIDTH_MULTIPLIER
            drawLine(pointerPos, 0f, pointerPos, height.toFloat(), pointer)
            if (pointerPos > maxPointerPosition) maxPointerPosition = pointerPos.toInt()

            activity.apply {
                runOnUiThread {
                    val requiredWidth = maxPointerPosition + EXTRA_WIDTH
                    if (requiredWidth > width)
                        holder.setFixedSize(requiredWidth, height)

                    // Auto scrolling
                    if (pointerPos > AUTOSCROLL_START)
                        tracksScrollView.scrollX = (pointerPos - AUTOSCROLL_START).toInt()
                }
            }
        }
    }

    private val trackHeight
        get() = height / TRACKS_NUMBER

    private fun trackX(trackId: Byte) = Pair(
        (trackHeight * trackId + PADDING_X).toFloat(),
        (trackHeight * (trackId + 1) - PADDING_X).toFloat()
    )

    companion object {
        private const val PADDING_X = 20
        private const val WIDTH_MULTIPLIER = 0.05f
        private const val EXTRA_WIDTH = 500
        private const val AUTOSCROLL_START = 500
    }

    private lateinit var thread: TracksDrawThread

    override fun surfaceCreated(holder: SurfaceHolder) {
        maxPointerPosition = width - EXTRA_WIDTH
        thread = TracksDrawThread(this).apply {
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.join()
    }
}