#include "WavePlayer.h"
#include "../AudioEngine.h"

void WavePlayer::fillBuffer(float *buffer, size_t numFrames) {
    for (int32_t i = 0; i < numFrames; i++) {
        float sampleValue = 0;
        for (auto & channel : AudioEngine::getChannels()) {
            sampleValue += channel->nextSample();
        }
        buffer[i] = applyFX(sampleValue);
    }
}
