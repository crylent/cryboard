#include "Wave.h"

#ifndef AUDIO_ENGINE_H
#include "AudioEngine.h"
#endif

#ifndef LOG_H
#include "log.h"
#endif

Wave::Wave(const shared_ptr<WaveInstrument>& instrument, float frequency, float amplitude) {
    mInstrument = instrument;
    mFrequency = frequency;
    mAmplitude = amplitude;
    mPhaseIncrement = float(mFrequency * 2 * M_PI / (double) AudioEngine::getSampleRate());
}

float Wave::nextSample() {
    float val = mAmplitude * mInstrument->calc(mPhase);
    mPhase += mPhaseIncrement;
    return val;
}