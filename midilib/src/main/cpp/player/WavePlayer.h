#ifndef MULTIWAVE_GENERATOR_H
#define MULTIWAVE_GENERATOR_H

#include "SoundPlayer.h"

class WavePlayer : public SoundPlayer {
public:
    void fillBuffer(float* buffer, int32_t numFrames) override;
};


#endif //MULTIWAVE_GENERATOR_H
