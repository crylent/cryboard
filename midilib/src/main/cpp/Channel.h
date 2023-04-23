#ifndef CHANNEL_H
#define CHANNEL_H

#ifndef WAVE_INSTRUMENT_H
#include "instrument/WaveInstrument.h"
#endif

#ifndef WAVE_H
#include "Wave.h"
#endif

#ifndef _LIBCPP_UNORDERED_MAP
#include <unordered_map>
#endif

using namespace std;

class Channel {
public:
    Channel();

    static void setDefaultInstrument(shared_ptr<WaveInstrument> instrument);
    void setInstrument(shared_ptr<WaveInstrument> instrument);

    void noteOn(int8_t note, float amplitude);
    void noteOff(int8_t note);

    float nextSample();

private:
    static shared_ptr<WaveInstrument> mDefaultInstrument;
    shared_ptr<WaveInstrument> mInstrument;
    mutex mLock;
    unordered_map<int8_t, unique_ptr<Wave>> mWaves = unordered_map<int8_t, unique_ptr<Wave>>();
};


#endif //CHANNEL_H
