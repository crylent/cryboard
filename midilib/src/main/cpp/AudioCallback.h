#ifndef AUDIO_CALLBACK_H
#define AUDIO_CALLBACK_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef SOUND_GENERATOR_H
#include "generator/SoundGenerator.h"
#endif

using namespace oboe;

class AudioCallback : public AudioStreamDataCallback {
public:
    DataCallbackResult onAudioReady(AudioStream* audioStream, void* audioData, int32_t numFrames);

    AudioCallback(const std::shared_ptr<SoundGenerator>& generator);

private:
    std::shared_ptr<SoundGenerator> mSoundGenerator;
};


#endif //AUDIO_CALLBACK_H
