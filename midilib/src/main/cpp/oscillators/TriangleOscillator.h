#ifndef TRIANGLE_OSCILLATOR_H
#define TRIANGLE_OSCILLATOR_H

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

class TriangleOscillator: public Oscillator {
    using Oscillator::Oscillator;

    float evalVoice(double time, float frequency, float extraPhase) override;
};


#endif //TRIANGLE_OSCILLATOR_H
