#ifndef AUDIO_CALLBACK_H
#define AUDIO_CALLBACK_H

#include "oboe/Oboe.h"
#include "player/SoundPlayer.h"

using namespace oboe;

class AudioCallback : public AudioStreamDataCallback {
public:
    DataCallbackResult onAudioReady(AudioStream* audioStream, void* audioData, int32_t numFrames);

    AudioCallback(unique_ptr<SoundPlayer> player);

private:
    shared_ptr<SoundPlayer> mSoundPlayer;
};


#endif //AUDIO_CALLBACK_H
