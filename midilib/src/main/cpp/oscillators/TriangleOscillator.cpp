#include "TriangleOscillator.h"

float TriangleOscillator::calculate(double time, float frequency, int8_t overtoneFactor) {
    float p = calcPhase(time, frequency, overtoneFactor);
    return mAmplitude * float((abs(p) - M_PI_2) / M_PI_2);
}
