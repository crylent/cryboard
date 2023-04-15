#include "TriangleOscillator.h"

float TriangleOscillator::evalVoice(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return float((abs(p) - M_PI_2) / M_PI_2);
}
