#ifndef AUDIO_CALLBACK_H
#define AUDIO_CALLBACK_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef SOUND_PLAYER_H
#include "player/SoundPlayer.h"
#endif

using namespace oboe;

class AudioCallback : public AudioStreamDataCallback {
public:
    DataCallbackResult onAudioReady(AudioStream* audioStream, void* audioData, int32_t numFrames);

    AudioCallback(unique_ptr<SoundPlayer> generator);

private:
    shared_ptr<SoundPlayer> mSoundGenerator;
};


#endif //AUDIO_CALLBACK_H
