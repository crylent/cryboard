#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#ifndef _LIBCPP_CMATH
#include <cmath>
#endif

class Oscillator {
public:
    virtual float calculate(double time, float frequency, int8_t overtoneFactor) = 0;
    float eval(double time, float frequency);

    Oscillator(float amplitude, float phase, float freqFactor, int8_t overtones);

protected:
    float mAmplitude = 1;
    float mPhase = 0;
    float mFreqFactor = 1;
    int8_t mOvertones = 0;

    float calcPhase(double time, float frequency, int8_t overtoneFactor) const;
};


#endif //OSCILLATOR_H
