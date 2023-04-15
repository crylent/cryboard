#include "SinOscillator.h"

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

float SinOscillator::eval(double time, float frequency) {
    return mAmplitude * sinf(calcPhase(time, frequency));
}
