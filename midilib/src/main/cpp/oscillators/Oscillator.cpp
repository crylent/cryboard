#include "Oscillator.h"

Oscillator::Oscillator(float amplitude, float phase, float freqFactor, int8_t overtones) {
    mAmplitude = amplitude;
    mPhase = phase;
    mFreqFactor = freqFactor;
    mOvertones = overtones;
}

/*float Oscillator::calcPhase(float phase) const {
    return mPhase + mFreqFactor * (remainderf(phase, M_PI * 2) - float(M_PI));
}*/

float Oscillator::calcPhase(float phase, int8_t overtoneFactor) const {
    return mPhase + float(overtoneFactor) * mFreqFactor * (remainderf(phase, M_PI * 2) - float(M_PI));
}

float Oscillator::eval(float phase) {
    float value = 0;
    for (int8_t i = 1; i <= mOvertones + 1; i++) {
        value += calculate(phase, i) / powf(2, i);
    }
    return value;
}
