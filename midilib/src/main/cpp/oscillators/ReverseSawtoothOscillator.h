#ifndef REVERSE_SAWTOOTH_OSCILLATOR_H
#define REVERSE_SAWTOOTH_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class ReverseSawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;
};


#endif //REVERSE_SAWTOOTH_OSCILLATOR_H
