#ifndef INSTRUMENT_H
#define INSTRUMENT_H

#include "oboe/Oboe.h"

class Instrument {
public:
    Instrument() {};
    Instrument(float attack, float decay, float sustain, float release);
    Instrument(float attack, float decay, float sustain, float release,
               float attackSharpness, float decaySharpness, float releaseSharpness);

    void setEnvelope(float attack, float decay, float sustain, float release);
    void setAttack(float attack);
    void setDecay(float decay);
    void setSustain(float sustain);
    void setRelease(float release);
    void setEnvelopeSharpness(float attack, float decay, float release);
    void setAttackSharpness(float sharpness);
    void setDecaySharpness(float sharpness);
    void setReleaseSharpness(float sharpness);

    float eval(double time, int8_t note, double timeReleased);

protected:
    virtual float sample(double time, int8_t note) = 0;

private:
    float envelope(double time, double timeReleased) const;

    float mAttack = 0; // seconds (after noteOn)
    float mDecay = 0; // seconds (after noteOn)
    float mSustain = 0; // from 0.0 to 1.0
    float mRelease = 0; // seconds (after noteOff)

    float mAttackSharpness = 1;
    float mDecaySharpness = 1;
    float mReleaseSharpness = 1;

    static void sharpnessCheck(float sharpness);
};


#endif //INSTRUMENT_H
