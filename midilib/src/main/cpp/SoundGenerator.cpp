#include "SoundGenerator.h"

int32_t SoundGenerator::mBufferSize = 256;
int32_t SoundGenerator::mSampleRate = 48000;

int32_t SoundGenerator::getBufferSize() {
    return mBufferSize;
}

void SoundGenerator::init(int32_t numFrames, int32_t sampleRate) {
    if (numFrames <= 0) {
        throw std::invalid_argument("Invalid buffer size");
    }
    if (sampleRate < MIN_SAMPLE_RATE) {
        throw std::invalid_argument("Invalid sample rate");
    }
    mBufferSize = numFrames;
    mSampleRate = sampleRate;
}
