#include "ReverseSawtoothOscillator.h"

float ReverseSawtoothOscillator::evalVoice(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return float(-p / M_PI);
}
