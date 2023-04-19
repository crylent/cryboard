#ifndef MULTIWAVE_GENERATOR_H
#define MULTIWAVE_GENERATOR_H

#ifndef SOUND_GENERATOR_H
#include "SoundGenerator.h"
#endif

class MultiwaveGenerator : public SoundGenerator {
public:
    void fillBuffer(float* buffer, int32_t numFrames) override;
};


#endif //MULTIWAVE_GENERATOR_H
