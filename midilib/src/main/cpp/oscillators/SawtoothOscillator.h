#ifndef SAWTOOTH_OSCILLATOR_H
#define SAWTOOTH_OSCILLATOR_H

#include "Oscillator.h"

class SawtoothOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;
};


#endif //SAWTOOTH_OSCILLATOR_H
