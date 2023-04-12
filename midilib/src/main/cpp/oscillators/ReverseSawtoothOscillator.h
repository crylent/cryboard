#ifndef REVERSE_SAWTOOTH_OSCILLATOR_H
#define REVERSE_SAWTOOTH_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class ReverseSawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float calculate(float phase, int8_t overtoneFactor) override;
};


#endif //REVERSE_SAWTOOTH_OSCILLATOR_H
