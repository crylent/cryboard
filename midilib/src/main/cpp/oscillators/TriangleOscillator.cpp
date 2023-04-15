#include "TriangleOscillator.h"

float TriangleOscillator::eval(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return mAmplitude * float((abs(p) - M_PI_2) / M_PI_2);
}
