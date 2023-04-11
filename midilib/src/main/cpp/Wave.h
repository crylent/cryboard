#ifndef WAVE_H
#define WAVE_H

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

using namespace std;

class Wave {
public:
    Wave(const shared_ptr<WaveInstrument>& instrument, float frequency, float amplitude);

    float nextSample();

private:
    shared_ptr<WaveInstrument> mInstrument;
    float mPhase = 0;
    float mFrequency;
    float mAmplitude;

    float mPhaseIncrement;
};


#endif //WAVE_H
