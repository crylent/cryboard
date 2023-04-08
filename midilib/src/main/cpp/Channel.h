#ifndef CHANNEL_H
#define CHANNEL_H

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

#ifndef WAVE_H
#include "Wave.h"
#endif

#ifndef _LIBCPP_UNORDERED_MAP
#include <unordered_map>
#endif

class Channel {
public:
    Channel();

    static void setDefaultInstrument(WaveInstrument* instrument);
    void setInstrument(WaveInstrument* instrument);

    void noteOn(int8_t note, float amplitude);
    void noteOff(int8_t note);

    float nextSample();

private:
    static WaveInstrument* mDefaultInstrument;
    WaveInstrument* mInstrument;
    std::unordered_map<int8_t, Wave*> mWaves = std::unordered_map<int8_t, Wave*>();

    Wave * newWave(float frequency, float amplitude);
};


#endif //CHANNEL_H
