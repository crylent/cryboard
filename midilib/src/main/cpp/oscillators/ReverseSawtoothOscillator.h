#ifndef REVERSE_SAWTOOTH_OSCILLATOR_H
#define REVERSE_SAWTOOTH_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class ReverseSawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float eval(double time, float frequency) override;
};


#endif //REVERSE_SAWTOOTH_OSCILLATOR_H
