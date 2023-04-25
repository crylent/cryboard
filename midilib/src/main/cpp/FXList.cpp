#include "FXList.h"

/**
 * Adds effect to list.
 * @param fx effect to be applied
 * @return position of effect in effects list
 */
uint8_t FXList::addEffect(unique_ptr<SoundFX> fx) {
    mEffects.push_back(move(fx));
    return mEffects.size() - 1;
}

/**
 * Removes all effects from list.
 */
void FXList::clearEffects() {
    mEffects.clear();
}

SoundFX &FXList::getEffect(uint8_t i) {
    return *mEffects[i];
}

float FXList::applyFX(float sample) {
    for (auto & fx : mEffects) {
        sample = fx->process(sample);
    }
    return sample;
}
