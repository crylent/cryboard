#ifndef FX_LIST_H
#define FX_LIST_H

#include "soundfx/SoundFX.h"
#include <memory>
#include <vector>

using namespace std;

class FXList {
public:
    uint8_t addEffect(unique_ptr<SoundFX> fx);
    SoundFX& getEffect(uint8_t i);
    void clearEffects();

    float applyFX(float sample);

private:
    vector<unique_ptr<SoundFX>> mEffects;
};


#endif //FX_LIST_H
