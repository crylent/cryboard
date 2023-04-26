#include "SoundFX.h"
#include "../AudioEngine.h"

SoundFX::SoundFX() {
    mTimeIncrement = AudioEngine::getTimeIncrement();
}
