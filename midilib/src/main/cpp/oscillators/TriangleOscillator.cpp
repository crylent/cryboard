#include "TriangleOscillator.h"

float TriangleOscillator::calculate(float phase, int8_t overtoneFactor) {
    float p = calcPhase(phase, overtoneFactor);
    return mAmplitude * float((abs(p) - M_PI_2) / M_PI_2);
}
