#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef AUDIO_CALLBACK_H
#include "AudioCallback.h"
#endif

#ifndef WAVE_GENERATOR_H
#include "WaveGenerator.h"
#endif

#ifndef MULTIWAVE_GENERATOR_H
#include "MultiwaveGenerator.h"
#endif

#ifndef CHANNEL_H
#include "Channel.h"
#endif

using namespace oboe;

class AudioEngine {
public:
    static Result start(SharingMode sharingMode, int32_t sampleRate);
    static Result start();
    static Result stop();

    static void initChannels();

    static int32_t getSampleRate();

    static std::vector<Channel*> getChannels();
    static int8_t getNumChannels();

    static void noteOn(int8_t channel, int8_t note, float amplitude);
    static void noteOff(int8_t channel, int8_t note);

private:
    static std::shared_ptr<oboe::AudioStream> mStream;
    static std::mutex mLock;
    static SharingMode mSharingMode;
    static int32_t mSampleRate;
    static int32_t mBufferSize;

    static const int8_t mNumChannels = 16;
    static std::vector<Channel*> mChannels;
};

#endif //AUDIO_ENGINE_H
