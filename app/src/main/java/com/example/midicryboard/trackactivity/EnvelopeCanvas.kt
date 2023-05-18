package com.example.midicryboard.trackactivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.midicryboard.Theme
import com.example.midilib.instrument.Instrument
import kotlin.math.pow

class EnvelopeCanvas(context: Context, attrs: AttributeSet): SurfaceView(context, attrs), SurfaceHolder.Callback {
    var instrument: Instrument? = null
    private val backgroundColor = Theme.switchOnTheme(context, Color.WHITE, Color.BLACK)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val paintA = Paint(paint).apply { color = ATTACK_COLOR }
    private val paintD = Paint(paint).apply { color = DECAY_COLOR }
    private val paintS = Paint(paint).apply { color = SUSTAIN_COLOR }
    private val paintR = Paint(paint).apply { color = RELEASE_COLOR }

    private lateinit var thread: EnvelopeDrawThread

    init {
        holder.addCallback(this)
    }

    private var time = 0f
    private var fullTime = 0f

    fun redraw(canvas: Canvas) {
        canvas.also {
            it.drawColor(backgroundColor)
            if (instrument == null) return
            val a = instrument!!.attack
            val d = instrument!!.decay
            val s = instrument!!.sustain
            val r = instrument!!.release
            val sTime = (a + d + r) * 0.5f
            fullTime = (a + d + sTime + r) * 1.1f
            time = 0f
            drawFragment(it, paintA, a) { t ->
                (t / a).pow(1 / instrument!!.attackSharpness)
            }
            drawFragment(it, paintD, a + d) { t ->
                ((a + d - t) / d).pow(instrument!!.decaySharpness) * (1 - s) + s
            }
            drawFragment(it, paintS, a + d + sTime) { s }
            drawFragment(it, paintR, a + d + sTime + r) { t ->
                ((a + d + sTime + r - t) / r).pow(instrument!!.releaseSharpness) * s
            }

            // early release
            val d2 = d * 0.5f
            time = a + d2
            drawFragment(it, paintR, a + d2 + r) { t ->
                ((a + d2 + r - t) / r).pow(instrument!!.releaseSharpness) *
                        ((1 - d2 / d).pow(instrument!!.decaySharpness) * (1 - s) + s)
            }
        }
    }

    private fun drawFragment(canvas: Canvas, paint: Paint, maxT: Float, f: (Float)->Float) {
        val path = Path()
        var firstPoint = true
        while (time <= maxT) {
            val x = width * time / fullTime
            val y = height * (1 - 0.9f * f(time))
            if (firstPoint) {
                path.moveTo(x, y)
                firstPoint = false
            } else {
                path.lineTo(x, y)
            }
            time += TIME_STEP
        }
        canvas.drawPath(path, paint)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (isInEditMode) {
            holder.unlockCanvasAndPost(null)
            return
        }
        thread = EnvelopeDrawThread(this).apply {
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    companion object {
        private const val TIME_STEP = 0.005f

        private const val ATTACK_COLOR = Color.RED
        private const val DECAY_COLOR = Color.GREEN
        private const val SUSTAIN_COLOR = Color.YELLOW
        private const val RELEASE_COLOR = Color.BLUE
    }
}