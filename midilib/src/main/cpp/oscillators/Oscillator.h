#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#ifndef _LIBCPP_CMATH
#include <cmath>
#endif

#ifndef DETUNE_H
#include "Detune.h"
#endif

using namespace std;

class Oscillator : enable_shared_from_this<Oscillator> {
public:
    float eval(double time, float frequency);
    virtual float evalVoice(double time, float frequency) = 0;

    Oscillator(float amplitude, float phase = 0, float freqFactor = 1);
    virtual ~Oscillator() = default;

    void enable();
    void disable();

    void setAmplitude(float amplitude);
    void setPhase(float phase);
    void setFreqFactor(float freqFactor);

    shared_ptr<float> a;

    shared_ptr<Detune> setDetune(uint8_t unisonVoices, float detune);
    void setDetune(const shared_ptr<Detune>& detune);
    shared_ptr<Detune> getDetune();
    void clearDetune();

protected:
    float calcPhase(double time, float frequency) const;

private:
    float mAmplitude = 1;
    float mPhase = 0;
    float mFreqFactor = 1;

    shared_ptr<Detune> mDetune = nullptr;

    bool mEnabled = true;
};

#endif //OSCILLATOR_H
