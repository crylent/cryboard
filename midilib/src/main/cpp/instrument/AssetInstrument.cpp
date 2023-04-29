#include "AssetInstrument.h"

#include <utility>
#include "../AudioEngine.h"
#include "samplerate.h"

//AAssetManager* AssetInstrument::mAssets = nullptr;

float AssetInstrument::sample(double time, int8_t note) {
    auto i = (size_t) round(time / AudioEngine::getTimeIncrement());
    return mSamples[note][0][i];
}

void AssetInstrument::loadAsset(int8_t note, vector<uint8_t>& wavData) {
    AudioFile<float> audioFile;
    //AAsset* asset = AAssetManager_open(mAssets.get(), filename.c_str(), AASSET_MODE_BUFFER);
    //AAsset* asset = AAssetManager_open(mAssets, "DRUM STICK-001.wav", AASSET_MODE_BUFFER);
    //AAsset_getBuffer(asset);
    //size_t assetSize = AAsset_getLength64(asset);
    //size_t assetSize = 5000;
    //uint8_t * buffer[assetSize];
    //AAsset_read(asset, buffer, assetSize);
    audioFile.loadFromMemory(wavData);
    if (AudioEngine::getSampleRate() != audioFile.getSampleRate()) {
        double ratio = double(AudioEngine::getSampleRate()) / double(audioFile.getSampleRate());
        size_t inSamples = audioFile.getNumSamplesPerChannel();
        size_t outSamples = ceil(double(inSamples) * ratio);
        unique_ptr<SRC_DATA> data;
        data->data_in = audioFile.samples[0].data();
        data->data_out = mSamples[note][0].data();
        data->input_frames = long(inSamples);
        data->output_frames = long(outSamples);
        data->src_ratio = ratio;
        src_simple(data.get(), SRC_SINC_FASTEST, 1);
    } else {
        mSamples[note] = audioFile.samples;
    }
}

/*void AssetInstrument::setAssetManager(AAssetManager* mgr) {
    //mAssets = unique_ptr<AAssetManager>(mgr);
    mAssets = mgr;
}*/