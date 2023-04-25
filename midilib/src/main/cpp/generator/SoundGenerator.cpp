#include "SoundGenerator.h"

float SoundGenerator::applyFX(float sample) {
    return mEffects->applyFX(sample);
}

shared_ptr<FXList> SoundGenerator::getEffects() {
    return mEffects;
}
