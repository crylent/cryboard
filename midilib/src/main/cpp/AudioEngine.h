#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef AUDIO_CALLBACK_H
#include "AudioCallback.h"
#endif

#ifndef MULTIWAVE_GENERATOR_H
#include "player/WavePlayer.h"
#endif

#ifndef CHANNEL_H
#include "Channel.h"
#endif

using namespace std;
using namespace oboe;

#define AUTO_DEFINITION -1

class AudioEngine {
public:
    static Result start(SharingMode sharingMode, int32_t sampleRate = AUTO_DEFINITION, int32_t bufferSize = AUTO_DEFINITION);
    static Result start();
    static Result stop();

    static void initChannels();

    static int32_t getSampleRate();
    static int32_t getBufferSize();
    static double getTimeIncrement();

    static FXList& getMasterFX();

    static vector<unique_ptr<Channel>>& getChannels();
    static int8_t getNumChannels();

    static void noteOn(int8_t channel, int8_t note, float amplitude);
    static void noteOff(int8_t channel, int8_t note);
    static void allNotesOff(int8_t channel);

private:
    static shared_ptr<oboe::AudioStream> mStream;
    static mutex mLock;
    static SharingMode mSharingMode;
    static int32_t mSampleRate;
    static double mTimeIncrement;
    static int32_t mBufferSize;

    static shared_ptr<FXList> mMasterEffects;

    static const int8_t mNumChannels = 16;
    static vector<unique_ptr<Channel>> mChannels;
};

#endif //AUDIO_ENGINE_H
