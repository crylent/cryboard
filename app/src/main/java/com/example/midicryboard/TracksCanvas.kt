package com.example.midicryboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.midicryboard.activity.MainActivity
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn

class TracksCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {
    private val p = Paint().apply { color = Color.MAGENTA }
    private val markup = Paint().apply { color = Color.GRAY }
    private val pointer = Paint().apply { color = Color.RED }
    private val backgroundColor = when(Theme.getTheme(context)) {
        Theme.DARK -> Color.BLACK
        Theme.LIGHT -> Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        var newMinWidth = 0
        canvas.apply {
            drawColor(backgroundColor)
            try {
                // Draw notes
                //var eventPointMax = 0f
                var notesOn = 0
                var noteOnPos = 0f
                Midi.processEvents { trackId, event ->
                    val eventPos = event.tick * WIDTH_MULTIPLIER
                    if (width + EXTRA_WIDTH < eventPos) newMinWidth = eventPos.toInt() + EXTRA_WIDTH
                    //if (eventPos > eventPointMax) eventPointMax = eventPos
                    if (event is NoteOn) {
                        if (notesOn == 0) noteOnPos = eventPos
                        notesOn += 1
                    }
                    else if (event is NoteOff) {
                        notesOn -= 1
                        if (notesOn == 0) {
                            val x = trackX(trackId)
                            drawRect(noteOnPos, x.first, eventPos, x.second, p)
                        }
                    }
                }
                //minimumWidth = eventPointMax.toInt() + EXTRA_WIDTH

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

                // Auto scrolling
                if (pointerPos > AUTOSCROLL_START)
                    (context as MainActivity).tracksScrollView.scrollX = (pointerPos - AUTOSCROLL_START).toInt()
            }
            catch (_: UnsatisfiedLinkError) {}
            catch (_: NoClassDefFoundError) {}
        }
        if (newMinWidth != 0) minimumWidth = newMinWidth
        else invalidate()
    }

    private val trackHeight
        get() = height / TRACKS_NUMBER

    private fun trackX(trackId: Int) = Pair(
        (trackHeight * trackId + PADDING_X).toFloat(),
        (trackHeight * (trackId + 1) - PADDING_X).toFloat()
    )

    companion object {
        private const val PADDING_X = 20
        private const val WIDTH_MULTIPLIER = 0.05f
        private const val EXTRA_WIDTH = 200
        private const val AUTOSCROLL_START = 500
    }
}