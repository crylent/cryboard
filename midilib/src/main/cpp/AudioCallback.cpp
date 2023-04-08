#include "AudioCallback.h"

#ifndef LOG_H
#include "log.h"
#endif

AudioCallback::AudioCallback(SoundGenerator* generator) {
    mSoundGenerator = generator;
}


DataCallbackResult
AudioCallback::onAudioReady(AudioStream *audioStream, void *audioData, int32_t numFrames) {
    auto* floatData = static_cast<float*>(audioData);
    mSoundGenerator->fillBuffer(floatData);
    return DataCallbackResult::Continue;
}