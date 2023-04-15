#ifndef WAVE_INSTRUMENT_H
#define WAVE_INSTRUMENT_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

class WaveInstrument {
public:
    WaveInstrument() {};
    WaveInstrument(float attack, float decay, float sustain, float release);

    void setEnvelope(float attack, float decay, float sustain, float release);
    void setAttack(float attack);
    void setDecay(float decay);
    void setSustain(float sustain);
    void setRelease(float release);

    float eval(double time, float frequency, double timeReleased);

protected:
    virtual float wave(double time, float frequency) = 0;

private:
    float envelope(double time, double timeReleased) const;

    float mAttack = 0; // seconds (after noteOn)
    float mDecay = 0; // seconds (after noteOn)
    float mSustain = 0; // from 0.0 to 1.0
    float mRelease = 0; // seconds (after noteOff)
};


#endif //WAVE_INSTRUMENT_H
