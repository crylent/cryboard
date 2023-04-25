#ifndef FX_LIST_H
#define FX_LIST_H

#ifndef SOUNDFX_H
#include "soundfx/SoundFX.h"
#endif

#ifndef _LIBCPP_MEMORY
#include <memory>
#endif

#ifndef _LIBCPP_VECTOR
#include <vector>
#endif

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
