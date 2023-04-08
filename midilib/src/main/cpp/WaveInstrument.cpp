#include "WaveInstrument.h"

float WaveInstrument::fadeOut(float phase) const {
    return expf(-mDamping * phase);
}

float WaveInstrument::calc(float phase) {
    return wave(phase) * fadeOut(phase);
}

void WaveInstrument::setDamping(float damping) {
    mDamping = damping;
}

WaveInstrument::WaveInstrument(float damping) {
    mDamping = damping;
}
