#include "MultiwaveGenerator.h"

#ifndef AUDIO_ENGINE_H
#include "AudioEngine.h"
#endif

void MultiwaveGenerator::fillBuffer(float *buffer) {
    for (int32_t i = 0; i < mBufferSize; i++) {
        float sampleValue = 0;
        for (auto & channel : AudioEngine::getChannels()) {
            sampleValue += channel->nextSample();
        }
        buffer[i] = sampleValue;
    }
}
