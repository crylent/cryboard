#include "AudioCallback.h"

#include <utility>

#ifndef LOG_H
#include "log.h"
#endif

AudioCallback::AudioCallback(const std::shared_ptr<SoundGenerator>& generator) {
    mSoundGenerator = generator;
}


DataCallbackResult
AudioCallback::onAudioReady(AudioStream *audioStream, void *audioData, int32_t numFrames) {
    auto* floatData = static_cast<float*>(audioData);
    mSoundGenerator->fillBuffer(floatData);
    return DataCallbackResult::Continue;
}