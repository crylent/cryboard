#include "SawtoothOscillator.h"

float SawtoothOscillator::calculate(float phase, int8_t overtoneFactor) {
    float p = calcPhase(phase, overtoneFactor);
    return mAmplitude * float(p / M_PI);
}
