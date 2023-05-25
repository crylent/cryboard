package com.example.midicryboard.mainactivity

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.plus
import com.example.midicryboard.Metronome
import com.example.midicryboard.Midi
import com.example.midicryboard.Theme
import com.example.midicryboard.TrackList
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn

class TracksCanvas(context: Context, attrs: AttributeSet): SurfaceView(context, attrs), SurfaceHolder.Callback {
    private val p = Paint().apply { color = Color.MAGENTA }
    private val markup = Paint().apply { color = Color.GRAY }
    private val staticPointer = Paint().apply { color = Color.RED }
    private val movingPointer = Paint().apply { color = Color.YELLOW }
    private val backgroundColor = Theme.switchOnTheme(context, Color.WHITE, Color.BLACK)

    private var minWidth = 0

    init {
        holder.addCallback(this)
        isClickable = true
        setOnTouchListener { _, event ->
            performClick() || detector.onTouchEvent(event)
        }
    }

    private val activity = if (isInEditMode) null else context as MainActivity

    private var fullRedraw = true

    fun callFullRedraw() {
        fullRedraw = true
    }

    private val regions = mutableListOf<Region>().apply {
        TrackList.linkCollection(this) { add(Region()) }
    }

    private var maxPointerPos = 0

    fun postCanvas() {
        holder.lockCanvas()?.let {
            synchronized(holder) {
                redraw(it)
                holder.unlockCanvasAndPost(it)
            }
        }
    }

    private fun redraw(canvas: Canvas) {
        canvas.apply {
            drawColor(backgroundColor)
            val selected = activity!!.selectedTrack
            regions[selected.toInt()].setEmpty()
            // Draw notes
            var notesOn = 0
            var noteOnPos = 0f
            Midi.processEvents(
                if (fullRedraw) Midi.ALL_TRACKS
                else selected
            ) { trackId, event ->
                val eventPos = Midi.ticksToTime(event.tick) * WIDTH_MULTIPLIER
                if (event is NoteOn) {
                    if (notesOn == 0) noteOnPos = eventPos
                    notesOn += 1
                } else if (event is NoteOff) {
                    notesOn -= 1
                    if (notesOn == 0) {
                        val y = trackY(trackId)
                        regions[trackId.toInt()] = regions[trackId.toInt()].plus(Rect(
                            noteOnPos.toInt(), y.first.toInt(),
                            eventPos.toInt(), y.second.toInt()
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
            val barDelta = Metronome.period * Metronome.signature.num * WIDTH_MULTIPLIER
            while (barPos < width) {
                barPos += barDelta
                drawLine(barPos, 0f, barPos, height.toFloat(), markup)
            }

            // Draw moving pointer (current position)
            val movingPointerPos = Midi.time * WIDTH_MULTIPLIER
            drawLine(movingPointerPos, 0f, movingPointerPos, height.toFloat(), movingPointer)

            // Adjust width of canvas
            if (movingPointerPos > maxPointerPos)
                maxPointerPos = movingPointerPos.toInt()
            val adjustedMaxPos = (Midi.lengthInMillis * WIDTH_MULTIPLIER).toInt()
            if (!Midi.playing && maxPointerPos > adjustedMaxPos)
                maxPointerPos = adjustedMaxPos

            // Draw static pointer (start position)
            val staticPointerPos = Midi.staticPointerTime * WIDTH_MULTIPLIER
            drawLine(staticPointerPos, 0f, staticPointerPos, height.toFloat(), staticPointer)

            activity.apply {
                runOnUiThread {
                    holder.setFixedSize((maxPointerPos + EXTRA_WIDTH).coerceAtLeast(minWidth), height)

                    // Auto scrolling
                    if (Midi.playing && movingPointerPos > AUTOSCROLL_START)
                        tracksScrollView.scrollX = (movingPointerPos - AUTOSCROLL_START).toInt()
                }
            }
        }
    }

    private val trackHeight
        get() = height / regions.size

    // Top and bottom of track
    private fun trackY(trackId: Byte) = Pair(
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
        minWidth = width
        maxPointerPos = width - EXTRA_WIDTH
        if (isInEditMode) {
            holder.unlockCanvasAndPost(null)
            return
        }
        thread = TracksDrawThread(this).apply {
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Midi.apply {
                if (playing) return false // do not move pointer when playing
                val tapTime = (e.x / WIDTH_MULTIPLIER).toLong()
                staticPointerTime = tapTime - tapTime % Metronome.period
                movingPointerInitTime = staticPointerTime
            }
            return true
        }
    })
}