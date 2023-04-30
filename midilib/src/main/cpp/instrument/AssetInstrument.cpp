#include "AssetInstrument.h"

#include <utility>
#include "../AudioEngine.h"
#include "samplerate.h"

#include "../log.h"

float AssetInstrument::sample(double time, int8_t note) {
    auto& data = mSamples[note];
    if (!data.empty()) {
        auto i = (size_t) round(time / AudioEngine::getTimeIncrement());
        if (mIsRepeatable) {
            i = i % data.size();
        }
        if (i < data.size()) return data[i];
    }
    return NAN;
}

void AssetInstrument::loadAsset(int8_t note, unique_ptr<vector<uint8_t>> wavData) {
    AudioFile<float> audioFile;
    audioFile.loadFromMemory(*wavData);
    if (AudioEngine::getSampleRate() != audioFile.getSampleRate()) {
        double ratio = double(AudioEngine::getSampleRate()) / double(audioFile.getSampleRate());
        size_t inSamples = audioFile.getNumSamplesPerChannel();
        size_t outSamples = ceil(double(inSamples) * ratio);
        auto data = make_unique<SRC_DATA>();
        data->data_in = audioFile.samples[0].data();
        auto* outData = new float[outSamples];
        data->data_out = outData;
        data->input_frames = long(inSamples);
        data->output_frames = long(outSamples);
        data->src_ratio = ratio;
        src_simple(data.get(), SRC_SINC_FASTEST, 1);
        mSamples[note].assign(outData, outData + outSamples);
    } else {
        mSamples[note] = audioFile.samples[0];
    }
}

void AssetInstrument::setRepeatable(bool isRepeatable) {
    mIsRepeatable = isRepeatable;
}

/*void AssetInstrument::setAssetManager(AAssetManager* mgr) {
    //mAssets = unique_ptr<AAssetManager>(mgr);
    mAssets = mgr;
}*/