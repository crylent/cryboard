#ifndef SOUND_GENERATOR_H
#define SOUND_GENERATOR_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef SOUNDFX_H
#include "../soundfx/SoundFX.h"
#endif

#ifndef FX_LIST_H
#include "../FXList.h"
#endif

using namespace std;

class SoundGenerator {
public:
    virtual ~SoundGenerator() = default;

    virtual void fillBuffer(float* buffer, int32_t numFrames) = 0;

    float applyFX(float sample);
    shared_ptr<FXList> getEffects();

private:
    shared_ptr<FXList> mEffects = make_unique<FXList>();
};


#endif //SOUND_GENERATOR_H
