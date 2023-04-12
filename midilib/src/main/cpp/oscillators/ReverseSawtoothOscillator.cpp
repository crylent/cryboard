#include "ReverseSawtoothOscillator.h"

float ReverseSawtoothOscillator::calculate(double time, float frequency, int8_t overtoneFactor) {
    float p = calcPhase(time, frequency, overtoneFactor);
    return mAmplitude * float(-p / M_PI);
}
