#include "Oscillator.h"

#ifndef _LIBCPP_STDEXCEPT
#include <stdexcept>
#endif

Oscillator::Oscillator(float amplitude, float phase, float freqFactor, uint8_t unisonVoices, float detune) {
    setAmplitude(amplitude);
    setPhase(phase);
    setFreqFactor(freqFactor);
    setDetune(unisonVoices, detune);
}

float Oscillator::eval(double time, float frequency) {
    if (!mEnabled) return 0;
    if (mUnisonVoices == 1) {
        return mAmplitude * evalVoice(time, frequency);
    }
    float minFrequency = frequency * (1 - mDetune);
    float delta = frequency * (2 * mDetune) / float(mUnisonVoices - 1);
    float sum = 0;
    for (uint8_t i = 0; i < mUnisonVoices; i++) {
        sum += evalVoice(time, minFrequency + float(i) * delta);
    }
    return mAmplitude * sum / float(mUnisonVoices);
}

float Oscillator::calcPhase(double time, float frequency) const {
    return (float) remainder(time * frequency * mFreqFactor * 2 * M_PI + mPhase, 2 * M_PI);
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
 * @param phase either in the range from 0 to 2π, or from -π to π
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
        throw std::invalid_argument("Frequency factor must be positive number");
    }
    mFreqFactor = freqFactor;
}

/**
 * Enables <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">detune</a>.
 * @param unisonVoices number of voices
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 */
void Oscillator::setDetune(uint8_t unisonVoices, float detune) {
    if (unisonVoices == 0) {
        throw std::invalid_argument("Unison voices must be positive number");
    }
    if (detune <= 0 || detune >= 1) {
        throw std::invalid_argument("Detune must be in the range from 0.0 non-inclusive to 1.0 non-inclusive");
    }
    mUnisonVoices = unisonVoices;
    mDetune = detune;
}

/**
 * Disables <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">detune</a>.
 */
void Oscillator::clearDetune() {
    mUnisonVoices = 1;
    mDetune = 0;
}

void Oscillator::enable() {
    mEnabled = true;
}

void Oscillator::disable() {
    mEnabled = false;
}
