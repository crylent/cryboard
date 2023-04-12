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

    void release();

private:
    shared_ptr<WaveInstrument> mInstrument;
    float mFrequency;
    float mAmplitude;
    float mTimeIncrement;

    float mTime = 0;
    float mTimeReleased = INFINITY;

    double time_double = 0;
    double inc_double;
};


#endif //WAVE_H
