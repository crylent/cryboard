#include "SoundFX.h"

#ifndef AUDIO_ENGINE_H
#include "../AudioEngine.h"
#endif

SoundFX::SoundFX() {
    mTimeIncrement = AudioEngine::getTimeIncrement();
}
