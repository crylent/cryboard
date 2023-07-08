#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#include <cmath>
#include "Detune.h"

using namespace std;

class Oscillator {
public:
    float eval(double time, float frequency);
    virtual float evalVoice(double time, float frequency, float extraPhase = 0) = 0;

    Oscillator() = default;
    Oscillator(float amplitude, float phase = 0, float freqFactor = 1);
    virtual ~Oscillator() = default;

    // copy constructor
    /*template<typename AnyOscillator>
    requires(is_base_of<Oscillator, AnyOscillator>())
    Oscillator(AnyOscillator &original) {
        mAmplitude = original.mAmplitude;
        mPhase = original.mPhase;
        mFreqFactor = original.mFreqFactor;
        mEnabled = original.mEnabled;
        if (original.mDetune) {
            mDetune = make_unique<Detune>(*this, *original.mDetune);
            //setDetune(original.mDetune.u);
        }
    }*/

    void enable();
    void disable();

    void setAmplitude(float amplitude);
    void setPhase(float phase);
    void setFreqFactor(float freqFactor);

    Detune& setDetune(uint8_t unisonVoices, float detune);
    Detune& setDetune();
    Detune& getDetune();
    void clearDetune();

protected:
    float calcPhase(double time, float frequency, float extraPhase = 0) const;

    void copyFrom(Oscillator& other) {
        mAmplitude = other.mAmplitude;
        mPhase = other.mPhase;
        mFreqFactor = other.mFreqFactor;
        mEnabled = other.mEnabled;
        if (other.mDetune) {
            mDetune = make_unique<Detune>(*this, *other.mDetune);
            //setDetune(original.mDetune.u);
        }
    }

private:
    float mAmplitude = 1;
    float mPhase = 0;
    float mFreqFactor = 1;

    unique_ptr<Detune> mDetune = nullptr;

    bool mEnabled = true;
};

#endif //OSCILLATOR_H
