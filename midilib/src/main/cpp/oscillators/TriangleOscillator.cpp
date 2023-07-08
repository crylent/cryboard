#include "TriangleOscillator.h"

float TriangleOscillator::evalVoice(double time, float frequency, float extraPhase) {
    float p = calcPhase(time, frequency);
    return float((abs(p) - M_PI_2) / M_PI_2);
}

TriangleOscillator::TriangleOscillator(Oscillator &other) {
    copyFrom(other);
}
