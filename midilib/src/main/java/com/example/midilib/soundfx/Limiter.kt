package com.example.midilib.soundfx

data class Limiter(
    var threshold: Float = 0.7f,
    var limit: Float = 0.99f,
    var attack: Float = 0f,
    var release: Float = 0.05f
) : SoundFX {
    override fun getId() = 1
    override fun getConfig() = mapOf(
        "threshold" to threshold,
        "limit" to limit,
        "attack" to attack,
        "release" to release
    )
}
