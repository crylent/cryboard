#include "Oscillator.h"

Oscillator::Oscillator(float amplitude, float phase, float freqFactor, int8_t overtones) {
    mAmplitude = amplitude;
    mPhase = phase;
    mFreqFactor = freqFactor;
    mOvertones = overtones;
}

float Oscillator::calcPhase(double time, float frequency, int8_t overtoneFactor) const {
    return float(remainder(time * frequency * double(overtoneFactor) * mFreqFactor * 2 * M_PI + mPhase, 2 * M_PI) - M_PI);
}

float Oscillator::eval(double time, float frequency) {
    float value = 0;
    for (int8_t i = 0; i <= mOvertones; i++) {
        value += calculate(time, frequency, int8_t(i + 1)) / powf(2, i);
    }
    return value;
}
