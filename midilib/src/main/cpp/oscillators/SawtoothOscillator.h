#ifndef SAWTOOTH_OSCILLATOR_H
#define SAWTOOTH_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float eval(double time, float frequency) override;
};


#endif //SAWTOOTH_OSCILLATOR_H
