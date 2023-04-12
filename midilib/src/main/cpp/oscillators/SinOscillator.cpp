#include "SinOscillator.h"

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

float SinOscillator::calculate(double time, float frequency, int8_t overtoneFactor) {
    return mAmplitude * sinf(calcPhase(time, frequency, overtoneFactor));
}
