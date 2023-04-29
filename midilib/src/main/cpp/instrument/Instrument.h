#ifndef INSTRUMENT_H
#define INSTRUMENT_H

#include "oboe/Oboe.h"

class Instrument {
public:
    Instrument() {};
    Instrument(float attack, float decay, float sustain, float release);

    void setEnvelope(float attack, float decay, float sustain, float release);
    void setAttack(float attack);
    void setDecay(float decay);
    void setSustain(float sustain);
    void setRelease(float release);

    float eval(double time, int8_t note, double timeReleased);

protected:
    virtual float sample(double time, int8_t note) = 0;

private:
    float envelope(double time, double timeReleased) const;

    float mAttack = 0; // seconds (after noteOn)
    float mDecay = 0; // seconds (after noteOn)
    float mSustain = 0; // from 0.0 to 1.0
    float mRelease = 0; // seconds (after noteOff)
};


#endif //INSTRUMENT_H
