package com.example.midicryboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn

class TracksCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {
    private val p = Paint().apply { color = Color.MAGENTA }
    private val background = Paint().apply {
        color = when(Theme.getTheme(context)) {
            Theme.DARK -> Color.BLACK
            Theme.LIGHT -> Color.WHITE
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            drawColor(background.color)
            try {
                var eventPointMax = 0f
                var notesOn = 0
                var noteOnPoint = 0f
                Midi.processEvents { trackId, event ->
                    val eventPoint = event.tick * WIDTH_MULTIPLIER
                    if (width < eventPoint) minimumWidth = eventPoint.toInt()
                    if (eventPoint > eventPointMax) eventPointMax = eventPoint
                    if (event is NoteOn) {
                        if (notesOn == 0) noteOnPoint = eventPoint
                        notesOn += 1
                    }
                    else if (event is NoteOff) {
                        notesOn -= 1
                        if (notesOn == 0) {
                            val x = trackX(trackId)
                            drawRect(noteOnPoint, x.first, eventPoint, x.second, p)
                        }
                    }
                }
                minimumWidth = eventPointMax.toInt()
            }
            catch (_: UnsatisfiedLinkError) {}
            catch (_: NoClassDefFoundError) {}
        }
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
    }
}