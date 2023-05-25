package com.example.midicryboard.mainactivity

class TracksDrawThread(private val tracksCanvas: TracksCanvas): Thread() {
    override fun run() {
        while (true) {
            tracksCanvas.postCanvas()
        }
    }
}