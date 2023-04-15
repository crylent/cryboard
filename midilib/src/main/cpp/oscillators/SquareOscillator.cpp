#include "SquareOscillator.h"

float SquareOscillator::evalVoice(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return ((p > 0) ? 1.0f : -1.0f);
}
