#ifndef SIN_OSCILLATOR_H
#define SIN_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SinOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float calculate(double time, float frequency, int8_t overtoneFactor) override;
};


#endif //SIN_OSCILLATOR_H
