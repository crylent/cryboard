#include "SineOscillator.h"

float SineOscillator::evalVoice(double time, float frequency, float extraPhase) {
    return sinf(calcPhase(time, frequency, extraPhase));
}

SineOscillator::SineOscillator(Oscillator &other) {
    copyFrom(other);
}
