#include "TriangleOscillator.h"

#ifndef LOG_H
#include "../log.h"
#endif

float TriangleOscillator::calculate(float time, float frequency, int8_t overtoneFactor) {
    float p = calcPhase(time, frequency, overtoneFactor);
    //LOGD("%f", p);
    return mAmplitude * float((abs(p) - M_PI_2) / M_PI_2);
}
