#ifndef ASSET_INSTRUMENT_H
#define ASSET_INSTRUMENT_H

#include "Instrument.h"
#include "../third_party/AudioFile.h"
//#include <android/asset_manager.h>

using namespace std;

class AssetInstrument : public Instrument {
public:
    using Instrument::Instrument;

    //void loadAsset(int8_t note, const string& filename);
    void loadAsset(int8_t note, vector<uint8_t>& wavData);

    float sample(double time, int8_t note) override;

    //static void setAssetManager(AAssetManager* mgr);

private:
    vector<AudioFile<float>::AudioBuffer> mSamples = vector<AudioFile<float>::AudioBuffer>(128);

    //static AAssetManager* mAssets;
};


#endif //ASSET_INSTRUMENT_H
