#include "AudioCallback.h"

#include <utility>

#ifndef LOG_H
#include "log.h"
#endif

AudioCallback::AudioCallback(unique_ptr<SoundPlayer> generator) {
    mSoundGenerator = move(generator);
}

DataCallbackResult AudioCallback::onAudioReady(
        AudioStream *audioStream, void *audioData, int32_t numFrames
        ) {
    auto* floatData = static_cast<float*>(audioData);
    mSoundGenerator->fillBuffer(floatData, numFrames);
    return DataCallbackResult::Continue;
}