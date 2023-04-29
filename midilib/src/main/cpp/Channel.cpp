#include "Channel.h"
#include <utility>
#include "NoteFrequency.h"

shared_ptr<Instrument> Channel::mDefaultInstrument = nullptr;

Channel::Channel() {
    mInstrument = mDefaultInstrument;
    if (!mInstrument) {
        throw logic_error("You must call setDefaultInstrument first");
    }
}

void Channel::setInstrument(shared_ptr<Instrument> instrument) {
    mInstrument = move(instrument);
}

void Channel::setDefaultInstrument(shared_ptr<Instrument> instrument) {
    mDefaultInstrument = move(instrument);
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
    auto wave = make_unique<Wave>(mInstrument, note, amplitude);
    mWaves[note] = move(wave);
}

void Channel::noteOff(int8_t note) {
    lock_guard<mutex> lockGuard(mLock);
    auto wave = mWaves.find(note);
    if (wave != mWaves.end()) {
        wave->second->release();
    }
}

void Channel::allNotesOff() {
    lock_guard<mutex> lockGuard(mLock);
    for (auto & wave : mWaves) {
        wave.second->release();
    }
}
