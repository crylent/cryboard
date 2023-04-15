#include "Oscillator.h"

#ifndef _LIBCPP_STDEXCEPT
#include <stdexcept>
#endif

Oscillator::Oscillator(float amplitude, float phase, float freqFactor) {
    setAmplitude(amplitude);
    setPhase(phase);
    setFreqFactor(freqFactor);
}

float Oscillator::calcPhase(double time, float frequency) const {
    return float(remainder(time * frequency * mFreqFactor * 2 * M_PI + mPhase, 2 * M_PI) - M_PI);
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