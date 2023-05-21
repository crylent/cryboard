#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#include "oboe/Oboe.h"
#include "AudioCallback.h"
#include "player/WavePlayer.h"
#include "Channel.h"

using namespace std;
using namespace oboe;

#define AUTO_DEFINITION -1

class AudioEngine {
public:
    static Result start(SharingMode sharingMode, int32_t sampleRate = AUTO_DEFINITION, int32_t bufferSize = AUTO_DEFINITION);
    static Result start();
    static Result pause();
    static Result resume();
    static Result stop();

    static void initChannels();

    static int32_t getSampleRate();
    static int32_t getBufferSize();
    static double getTimeIncrement();

    static FXList& getMasterFX();

    static vector<unique_ptr<Channel>>& getChannels();
    static uint8_t getNumChannels();

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

    static const uint8_t mNumChannels = 16;
    static vector<unique_ptr<Channel>> mChannels;
};

#endif //AUDIO_ENGINE_H
