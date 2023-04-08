#ifndef AUDIO_CALLBACK_H
#define AUDIO_CALLBACK_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef WAVE_GENERATOR_H
#include "WaveGenerator.h"
#endif

using namespace oboe;

class AudioCallback : public AudioStreamDataCallback {
public:
    DataCallbackResult onAudioReady(AudioStream* audioStream, void* audioData, int32_t numFrames);

    AudioCallback(SoundGenerator* generator);

private:
    SoundGenerator* mSoundGenerator;

    static constexpr int sampleRate = 48000;
    static constexpr float amplitude = 0.5f;
    static constexpr float frequency = 440;
    static constexpr double phaseIncrement = frequency * 2 * M_PI / (double) sampleRate;
    float phase = 0.0;
};


#endif //AUDIO_CALLBACK_H
