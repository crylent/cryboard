package com.example.midicryboard.trackactivity

class EnvelopeDrawThread(private val envelopeCanvas: EnvelopeCanvas): Thread() {

    private val holder = envelopeCanvas.holder

    override fun run() {
        while (true) {
            holder.lockCanvas()?.let {
                synchronized(holder) {
                    envelopeCanvas.redraw(it)
                }
                holder.unlockCanvasAndPost(it)
            }
        }
    }
}