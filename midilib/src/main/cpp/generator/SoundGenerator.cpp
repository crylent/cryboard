#include "SoundGenerator.h"

float SoundGenerator::applyFX(float sample) {
    for (auto & fx : mEffects) {
        sample = fx->process(sample);
    }
    return sample;
}

void SoundGenerator::addEffect(const shared_ptr<SoundFX>& fx) {
    mEffects.push_back(fx);
}

void SoundGenerator::clearEffects() {
    mEffects.clear();
}
