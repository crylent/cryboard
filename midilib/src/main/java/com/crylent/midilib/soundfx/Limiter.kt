package com.crylent.midilib.soundfx

class Limiter(
    threshold: Float = 0.7f,
    limit: Float = 0.99f,
    attack: Float = 0f,
    release: Float = 0.05f
) : SoundFX() {
    var threshold = threshold
        set(value) {
            field = value
            updateParameter(THRESHOLD, value)
        }
    var limit = limit
        set(value) {
            field = value
            updateParameter(LIMIT, value)
        }
    var attack = attack
        set(value) {
            field = value
            updateParameter(ATTACK, value)
        }
    var release = release
        set(value) {
            field = value
            updateParameter(RELEASE, value)
        }

    override fun getId() = 1
    override fun getConfig() = mapOf(
        THRESHOLD to threshold,
        LIMIT to limit,
        ATTACK to attack,
        RELEASE to release
    )

    override fun clone() = Limiter(threshold, limit, attack, release)
}
