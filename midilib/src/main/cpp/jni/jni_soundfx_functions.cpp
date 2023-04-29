#include "jni.h"

static FXList& getFXList(int8_t channel) {
    if (channel == -1) { // Master
        return AudioEngine::getMasterFX();
    } else {
        throw std::exception(); // Not implemented
    }
}