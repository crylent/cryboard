#include "SawtoothOscillator.h"

float SawtoothOscillator::evalVoice(double time, float frequency, float extraPhase) {
    float p = calcPhase(time, frequency, extraPhase);
    return float(p / M_PI);
}

SawtoothOscillator::SawtoothOscillator(Oscillator &other) {
    copyFrom(other);
}
