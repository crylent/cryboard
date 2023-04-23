#ifndef SQUARE_OSCILLATOR_H
#define SQUARE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SquareOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;
};


#endif //SQUARE_OSCILLATOR_H
