#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#ifndef _LIBCPP_CMATH
#include <cmath>
#endif

class Oscillator {
public:
    virtual float eval(double time, float frequency) = 0;

    Oscillator(float amplitude, float phase = 0, float freqFactor = 1);

    void setAmplitude(float amplitude);
    void setPhase(float phase);
    void setFreqFactor(float freqFactor);

protected:
    float mAmplitude = 1;
    float mPhase = 0;
    float mFreqFactor = 1;

    float calcPhase(double time, float frequency) const;
};


#endif //OSCILLATOR_H
