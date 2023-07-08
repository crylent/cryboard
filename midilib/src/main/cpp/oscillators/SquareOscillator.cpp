#include "SquareOscillator.h"

float SquareOscillator::evalVoice(double time, float frequency, float extraPhase) {
    float p = calcPhase(time, frequency, extraPhase);
    return ((p > 0) ? 1.0f : -1.0f);
}

SquareOscillator::SquareOscillator(Oscillator &other) {
    copyFrom(other);
}
