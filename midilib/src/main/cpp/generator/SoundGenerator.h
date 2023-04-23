#ifndef SOUND_GENERATOR_H
#define SOUND_GENERATOR_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef SOUNDFX_H
#include "../soundfx/SoundFX.h"
#endif

#ifndef _LIBCPP_VECTOR
#include <vector>
#endif

using namespace std;

class SoundGenerator {
public:
    virtual ~SoundGenerator() = default;

    virtual void fillBuffer(float* buffer, int32_t numFrames) = 0;

    float applyFX(float sample);

    void addEffect(unique_ptr<SoundFX> fx);
    void clearEffects();

private:
    vector<unique_ptr<SoundFX>> mEffects;
};


#endif //SOUND_GENERATOR_H
