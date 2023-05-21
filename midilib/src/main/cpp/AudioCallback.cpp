#include "AudioCallback.h"
#include <utility>
#include "log.h"

AudioCallback::AudioCallback(unique_ptr<SoundPlayer> player) {
    mSoundPlayer = move(player);
}

DataCallbackResult AudioCallback::onAudioReady(
        AudioStream *audioStream, void *audioData, int32_t numFrames
        ) {
    auto* floatData = static_cast<float*>(audioData);
    mSoundPlayer->fillBuffer(floatData, numFrames);
    return DataCallbackResult::Continue;
}