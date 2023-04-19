#include "MultiwaveGenerator.h"

#ifndef AUDIO_ENGINE_H
#include "../AudioEngine.h"
#endif

#ifndef _LIBCPP_VECTOR
#include <vector>
#endif

void MultiwaveGenerator::fillBuffer(float *buffer, int32_t numFrames) {
    for (int32_t i = 0; i < numFrames; i++) {
        float sampleValue = 0;
        for (auto & channel : AudioEngine::getChannels()) {
            sampleValue += channel->nextSample();
        }
        buffer[i] = applyFX(sampleValue);
    }
}
