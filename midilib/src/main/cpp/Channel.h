#ifndef CHANNEL_H
#define CHANNEL_H

#include "instrument/Instrument.h"
#include "Wave.h"
#include <unordered_map>

using namespace std;

class Channel {
public:
    Channel();

    static void setDefaultInstrument(shared_ptr<Instrument> instrument);
    void setInstrument(shared_ptr<Instrument> instrument);

    void noteOn(int8_t note, float amplitude);
    void noteOff(int8_t note);
    void allNotesOff();

    float nextSample();

private:
    static shared_ptr<Instrument> mDefaultInstrument;
    shared_ptr<Instrument> mInstrument;
    mutex mLock;
    unordered_map<int8_t, unique_ptr<Wave>> mWaves = unordered_map<int8_t, unique_ptr<Wave>>();
};


#endif //CHANNEL_H
