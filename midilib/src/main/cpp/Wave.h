#ifndef WAVE_H
#define WAVE_H

#ifndef WAVE_INSTRUMENT_H
#include "instrument/WaveInstrument.h"
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
    double mTimeIncrement;

    double mTime = 0;
    double mTimeReleased = INFINITY;
};


#endif //WAVE_H
