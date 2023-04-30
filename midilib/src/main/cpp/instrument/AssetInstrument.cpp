#include "AssetInstrument.h"

#include <utility>
#include "../AudioEngine.h"
#include "samplerate.h"
#include "../NoteFrequency.h"

uint8_t AssetInstrument::mResamplingQuality = SRC_SINC_FASTEST;

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
        resampleAndAssign(audioFile.samples[0], ratio, note);
    } else {
        mSamples[note] = audioFile.samples[0];
    }
}

void AssetInstrument::loadBaseAsset(int8_t baseNote, unique_ptr<vector<uint8_t>> wavData) {
    loadAsset(baseNote, move(wavData));
    for (int8_t note = 0; note >= 0; note++) {
        if (note == baseNote) continue;
        double ratio = NoteFrequency::get(baseNote) / NoteFrequency::get(note);
        resampleAndAssign(mSamples[baseNote], ratio, note);
    }
}

void AssetInstrument::resampleAndAssign(vector<float> &dataIn, double ratio, int8_t note) {
    size_t inSamples = dataIn.size();
    size_t outSamples = ceil(double(inSamples) * ratio);
    auto data = make_unique<SRC_DATA>();
    data->data_in = dataIn.data();
    auto* outData = new float[outSamples];
    data->data_out = outData;
    data->input_frames = long(inSamples);
    data->output_frames = long(outSamples);
    data->src_ratio = ratio;
    src_simple(data.get(), mResamplingQuality, 1);
    mSamples[note].assign(outData, outData + outSamples);
}

void AssetInstrument::setRepeatable(bool isRepeatable) {
    mIsRepeatable = isRepeatable;
}

void AssetInstrument::setResamplingQuality(uint8_t quality) {
    if (quality != SRC_LINEAR &&
        quality != SRC_ZERO_ORDER_HOLD &&
        quality != SRC_SINC_FASTEST &&
        quality != SRC_SINC_MEDIUM_QUALITY &&
        quality != SRC_SINC_BEST_QUALITY) throw invalid_argument("Invalid resampling quality");
    mResamplingQuality = quality;
}
