#include "SinOscillator.h"

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

float SinOscillator::calculate(float phase, int8_t overtoneFactor) {
    return mAmplitude * sinf(calcPhase(phase, overtoneFactor));
}
