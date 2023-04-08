#ifndef SOUND_GENERATOR_H
#define SOUND_GENERATOR_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif


class SoundGenerator {
public:
    static int32_t getBufferSize();

    static void init(int32_t numFrames, int32_t sampleRate);

    virtual void fillBuffer(float* buffer) = 0;

protected:
    static int32_t mBufferSize;
    static int32_t mSampleRate;

private:
    static constexpr int32_t MIN_SAMPLE_RATE = 8000;
};


#endif //SOUND_GENERATOR_H
