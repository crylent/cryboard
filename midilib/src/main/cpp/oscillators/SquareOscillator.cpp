#include "SquareOscillator.h"

float SquareOscillator::eval(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return mAmplitude * ((p > 0) ? 1.0f : -1.0f);
}
