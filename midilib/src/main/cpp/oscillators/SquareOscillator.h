#ifndef SQUARE_OSCILLATOR_H
#define SQUARE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class SquareOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float calculate(float time, float frequency, int8_t overtoneFactor) override;
};


#endif //SQUARE_OSCILLATOR_H
