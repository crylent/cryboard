package com.example.midilib.soundfx

interface SoundFX {
    fun getId(): Int
    fun getConfig(): Map<String, Float>

    fun getParameter(param: String): Float = getConfig()[param]!!
}