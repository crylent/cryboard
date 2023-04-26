#include "SoundPlayer.h"

float SoundPlayer::applyFX(float sample) {
    return mEffects->applyFX(sample);
}

shared_ptr<FXList> SoundPlayer::getEffects() {
    return mEffects;
}
