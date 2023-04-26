#ifndef SINE_OSCILLATOR_H
#define SINE_OSCILLATOR_H

#include "Oscillator.h"

class SineOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;
};


#endif //SINE_OSCILLATOR_H
