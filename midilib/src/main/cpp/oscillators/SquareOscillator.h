#ifndef SQUARE_OSCILLATOR_H
#define SQUARE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SquareOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float eval(double time, float frequency) override;
};


#endif //SQUARE_OSCILLATOR_H
