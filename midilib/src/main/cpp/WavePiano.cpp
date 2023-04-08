#include "WavePiano.h"

float WavePiano::wave(float phase) {
    float y = 0;
    for (uint8_t i = 1; i <= mOvertones + 1; i++) {
        y += sinf(float(i) * phase) / powf(2, i);
    }
    return y + mSaturation * y * y * y;
}

/**
 * Constructs new <code>WavePiano WaveInstrument</code> with specified configuration. \n
 * <code>WaveInstrument</code> objects are responsible for modelling of sound waves.
 * @param overtones increase saturation of sound. However, too large values make no sense.
 * @param saturation recommended values are 0 to 1.0
 * @param damping fade rate
 */
WavePiano::WavePiano(uint8_t overtones, float saturation, float damping) : WaveInstrument(damping) {
    setOvertones(overtones);
    setSaturation(saturation);
}

/**
 * Constructs new <code>WavePiano WaveInstrument</code> with default <b>damping</b> (fade rate). \n
 * <code>WaveInstrument</code> objects are responsible for modelling of sound waves.
 * @param overtones increase saturation of sound. However, too large values make no sense.
 * @param saturation recommended values are 0 to 1.0
 */
WavePiano::WavePiano(uint8_t overtones, float saturation) : WavePiano(overtones, saturation, 0.0004) {}

/**
 * Constructs new <code>WavePiano WaveInstrument</code> with default configuration. \n
 * <code>WaveInstrument</code> objects are responsible for modelling of sound waves.
 */
WavePiano::WavePiano() : WaveInstrument(0.0004) {}

/** <b>Overtones</b> increase saturation of sound. However, too large values make no sense.\n **/
void WavePiano::setOvertones(uint8_t overtones) {
    if (overtones < 0) {
        throw std::invalid_argument("Overtones can't be negative number");
    }
    mOvertones = overtones;
}

/** Recommended values for <b>saturation</b> are 0 to 1.0.\n **/
void WavePiano::setSaturation(float saturation) {
    mSaturation = saturation;
}
