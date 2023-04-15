#include "SinOscillator.h"

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

float SinOscillator::evalVoice(double time, float frequency) {
    return sinf(calcPhase(time, frequency));
}
