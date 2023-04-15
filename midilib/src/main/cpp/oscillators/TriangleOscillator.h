#ifndef TRIANGLE_OSCILLATOR_H
#define TRIANGLE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class TriangleOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float eval(double time, float frequency) override;
};


#endif //TRIANGLE_OSCILLATOR_H
