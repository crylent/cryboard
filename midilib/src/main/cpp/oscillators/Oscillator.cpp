#include "Oscillator.h"

Oscillator::Oscillator(float amplitude, float phase, float freqFactor) {
    setAmplitude(amplitude);
    setPhase(phase);
    setFreqFactor(freqFactor);
}

float Oscillator::eval(double time, float frequency) {
    if (!mEnabled) return 0;
    if (!mDetune) {
        return mAmplitude * evalVoice(time, frequency);
    }
    return mAmplitude * mDetune->process(time, frequency);
}

float Oscillator::calcPhase(double time, float frequency, float extraPhase) const {
    return (float) remainder(time * frequency * mFreqFactor * 2 * M_PI + mPhase + extraPhase, 2 * M_PI);
}

/**
 * Sets the amplitude, i.e. volume.
 * @param amplitude positive number from -1.0 to 1.0
 */
void Oscillator::setAmplitude(float amplitude) {
    mAmplitude = amplitude;
}

/**
 * Sets the phase shift.
 * @param phase radians, either in the range from 0 to 2π, or from -π to π
 */
void Oscillator::setPhase(float phase) {
    mPhase = phase;
}

/**
 * Sets the frequency factor.
 * @param freqFactor positive multiplier for frequency (1.0 for default frequencies)
 */
void Oscillator::setFreqFactor(float freqFactor) {
    if (freqFactor <= 0) {
        throw invalid_argument("Frequency factor must be positive number");
    }
    mFreqFactor = freqFactor;
}

/**
 * Constructs new <code>Detune</code> object and assigns it to the oscillator.
 * @param unisonVoices number of unison voices, 2 or more
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 * @return created <code>Detune</code> object
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
Detune& Oscillator::setDetune(uint8_t unisonVoices, float detune) {
    mDetune = make_unique<Detune>(*this, unisonVoices, detune);
    return *mDetune;
}

/**
 * Constructs new <code>Detune</code> object with default configuration and assigns it to the oscillator.
 * @return created <code>Detune</code> object
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
Detune& Oscillator::setDetune() {
    mDetune = make_unique<Detune>(*this);
    return *mDetune;
}

/**
 * @return oscillator's <code>Detune</code> object, <code>nullptr</code> if detune is not enabled
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
Detune& Oscillator::getDetune() {
    return *mDetune;
}

/**
 * Disables <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">detune</a>.
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
void Oscillator::clearDetune() {
    mDetune = nullptr;
}

/** Enables oscillator. */
void Oscillator::enable() {
    mEnabled = true;
}

/** Disables oscillator. */
void Oscillator::disable() {
    mEnabled = false;
}
