package com.example.midicryboard.mainactivity

class TracksDrawThread(private val tracksCanvas: TracksCanvas): Thread() {

    private val holder = tracksCanvas.holder

    override fun run() {
        while (true) {
            holder.lockCanvas()?.let {
                synchronized(holder) {
                    tracksCanvas.redraw(it)
                }
                holder.unlockCanvasAndPost(it)
            }
        }
    }
}