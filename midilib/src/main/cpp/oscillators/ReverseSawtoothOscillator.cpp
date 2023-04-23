#include "ReverseSawtoothOscillator.h"

float ReverseSawtoothOscillator::evalVoice(double time, float frequency, float extraPhase) {
    float p = calcPhase(time, frequency, extraPhase);
    return float(-p / M_PI);
}
