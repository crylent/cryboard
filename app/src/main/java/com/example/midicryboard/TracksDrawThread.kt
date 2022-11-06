package com.example.midicryboard

class TracksDrawThread(private val tracksCanvas: TracksCanvas): Thread() {

    private val holder = tracksCanvas.holder

    override fun run() {
        while (true) {
            val canvas = holder.lockCanvas()
            synchronized(holder) {
                tracksCanvas.redraw(canvas)
            }
            if (canvas != null) holder.unlockCanvasAndPost(canvas)
        }
    }
}