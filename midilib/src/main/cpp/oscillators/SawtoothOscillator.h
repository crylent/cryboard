#ifndef SAWTOOTH_OSCILLATOR_H
#define SAWTOOTH_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float calculate(float phase, int8_t overtoneFactor) override;
};


#endif //SAWTOOTH_OSCILLATOR_H
