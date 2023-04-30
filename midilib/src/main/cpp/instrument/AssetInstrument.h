#ifndef ASSET_INSTRUMENT_H
#define ASSET_INSTRUMENT_H

#include "Instrument.h"
#include "../third_party/AudioFile.h"

using namespace std;

class AssetInstrument : public Instrument {
public:
    using Instrument::Instrument;

    void loadAsset(int8_t note, unique_ptr<vector<uint8_t>> wavData);
    void setRepeatable(bool isRepeatable);

    float sample(double time, int8_t note) override;

private:
    vector<vector<float>> mSamples = vector<vector<float>>(SCHAR_MAX + 1);
    bool mIsRepeatable = false;
};


#endif //ASSET_INSTRUMENT_H
