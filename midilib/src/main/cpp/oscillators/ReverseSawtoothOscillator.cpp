#include "ReverseSawtoothOscillator.h"

float ReverseSawtoothOscillator::calculate(float phase, int8_t overtoneFactor) {
    float p = calcPhase(phase, overtoneFactor);
    return mAmplitude * float(-p / M_PI);
}
