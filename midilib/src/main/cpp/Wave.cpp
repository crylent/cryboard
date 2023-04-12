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
    mTimeIncrement = 1.0f / (float) AudioEngine::getSampleRate();
    inc_double = 1.0 / AudioEngine::getSampleRate();
    /*LOGD("float increment: %.20f", mTimeIncrement);
    LOGD("double increment: %.20f", inc_double);*/
}

float Wave::nextSample() {
    float val = mInstrument->eval(mTime, mFrequency, mTimeReleased);
    mTime += mTimeIncrement;
    time_double += inc_double;
    return val * mAmplitude;
}

void Wave::release() {
    mTimeReleased = mTime;
    /*LOGD("float time: %.20f", mTime);
    LOGD("double time: %.20f", time_double);
    LOGD("float phase: %.20f", remainderf(mTime * mFrequency * float(2 * M_PI), 2 * M_PI) - float(M_PI));
    LOGD("double phase: %.20f", remainder(time_double * mFrequency * 2 * M_PI, 2 * M_PI) - M_PI);*/
}
