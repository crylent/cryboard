#include "Wave.h"

#include <utility>
#include "AudioEngine.h"

Wave::Wave(shared_ptr<WaveInstrument> instrument, float frequency, float amplitude) {
    mInstrument = std::move(instrument);
    mFrequency = frequency;
    mAmplitude = amplitude;
    mTimeIncrement = AudioEngine::getTimeIncrement();
}

float Wave::nextSample() {
    float val = mInstrument->eval(mTime, mFrequency, mTimeReleased);
    mTime += mTimeIncrement;
    return val * mAmplitude;
}

void Wave::release() {
    mTimeReleased = mTime;
}
