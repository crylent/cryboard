#include "Channel.h"

#include <utility>

#ifndef NOTE_FREQUENCY_H
#include "NoteFrequency.h"
#endif

shared_ptr<WaveInstrument> Channel::mDefaultInstrument = nullptr;

Channel::Channel() {
    mInstrument = mDefaultInstrument;
    if (!mInstrument) {
        throw logic_error("You must call setDefaultInstrument first");
    }
}

void Channel::setInstrument(const shared_ptr<WaveInstrument>& instrument) {
    mInstrument = instrument;
}

shared_ptr<Wave> Channel::newWave(float frequency, float amplitude) {
    auto wave = make_shared<Wave>(mInstrument, frequency, amplitude);
    return wave;
}

float Channel::nextSample() {
    lock_guard<mutex> lockGuard(mLock);
    float sampleValue = 0;
    for (auto & wave : mWaves) {
        float waveValue = wave.second->nextSample();
        if (isnan(waveValue)) {
            mWaves.erase(wave.first);
        } else {
            sampleValue += waveValue;
        }
    }
    return sampleValue;
}

void Channel::noteOn(int8_t note, float amplitude) {
    lock_guard<mutex> lockGuard(mLock);
    auto wave = newWave(NoteFrequency::get(note), amplitude);
    mWaves[note] = wave;
}

void Channel::noteOff(int8_t note) {
    lock_guard<mutex> lockGuard(mLock);
    mWaves[note]->release();
}

void Channel::setDefaultInstrument(const shared_ptr<WaveInstrument>& instrument) {
    mDefaultInstrument = instrument;
}