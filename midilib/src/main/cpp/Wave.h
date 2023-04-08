#ifndef WAVE_H
#define WAVE_H

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

class Wave {
public:
    Wave(WaveInstrument* instrument, float frequency, float amplitude);

    float nextSample();

private:
    WaveInstrument* mInstrument{};
    float mPhase = 0;
    float mFrequency{};
    float mAmplitude{};

    float mPhaseIncrement{};
};


#endif //WAVE_H
