#ifndef TRIANGLE_OSCILLATOR_H
#define TRIANGLE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class TriangleOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float calculate(double time, float frequency, int8_t overtoneFactor) override;
};


#endif //TRIANGLE_OSCILLATOR_H
