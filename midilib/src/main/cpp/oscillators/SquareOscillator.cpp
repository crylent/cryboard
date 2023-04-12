#include "SquareOscillator.h"

float SquareOscillator::calculate(float phase, int8_t overtoneFactor) {
    float p = calcPhase(phase, overtoneFactor);
    return mAmplitude * ((p > 0) ? 1.0f : -1.0f);
}
