#ifndef REVERSE_SAWTOOTH_OSCILLATOR_H
#define REVERSE_SAWTOOTH_OSCILLATOR_H

#include "Oscillator.h"

class ReverseSawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;

public:
    ReverseSawtoothOscillator(Oscillator &other);
};


#endif //REVERSE_SAWTOOTH_OSCILLATOR_H
