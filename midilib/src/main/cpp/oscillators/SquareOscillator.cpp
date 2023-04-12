#include "SquareOscillator.h"

float SquareOscillator::calculate(double time, float frequency, int8_t overtoneFactor) {
    float p = calcPhase(time, frequency, overtoneFactor);
    return mAmplitude * ((p > 0) ? 1.0f : -1.0f);
}
