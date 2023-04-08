#ifndef WAVE_INSTRUMENT_H
#define WAVE_INSTRUMENT_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

class WaveInstrument {
public:
    WaveInstrument(float damping);

    float calc(float phase);

    void setDamping(float damping);

protected:
    virtual float wave(float phase) = 0;

    float fadeOut(float phase) const;

    float mDamping = 0;
};


#endif //WAVE_INSTRUMENT_H
