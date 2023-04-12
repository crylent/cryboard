#include "SawtoothOscillator.h"

float SawtoothOscillator::calculate(float time, float frequency, int8_t overtoneFactor) {
    float p = calcPhase(time, frequency, overtoneFactor);
    return mAmplitude * float(p / M_PI);
}
