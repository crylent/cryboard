#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#ifndef _LIBCPP_CMATH
#include <cmath>
#endif

class Oscillator {
public:
    float eval(double time, float frequency);

    Oscillator(float amplitude, float phase = 0, float freqFactor = 1, uint8_t unisonVoices = 1, float detune = 0);

    void setAmplitude(float amplitude);
    void setPhase(float phase);
    void setFreqFactor(float freqFactor);
    void setDetune(uint8_t unisonVoices, float detune);
    void clearDetune();

protected:
    virtual float evalVoice(double time, float frequency) = 0;
    float calcPhase(double time, float frequency) const;

private:
    float mAmplitude = 1;
    float mPhase = 0;
    float mFreqFactor = 1;

    uint8_t mUnisonVoices = 1;
    float mDetune = 0;
};


#endif //OSCILLATOR_H
