#ifndef SIN_OSCILLATOR_H
#define SIN_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SineOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency) override;
};


#endif //SIN_OSCILLATOR_H
