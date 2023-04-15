#include "SinOscillator.h"

float SinOscillator::evalVoice(double time, float frequency) {
    return sinf(calcPhase(time, frequency));
}
