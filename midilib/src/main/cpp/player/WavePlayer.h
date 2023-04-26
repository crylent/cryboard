#ifndef MULTIWAVE_GENERATOR_H
#define MULTIWAVE_GENERATOR_H

#ifndef SOUND_PLAYER_H
#include "SoundPlayer.h"
#endif

class WavePlayer : public SoundPlayer {
public:
    void fillBuffer(float* buffer, int32_t numFrames) override;
};


#endif //MULTIWAVE_GENERATOR_H
